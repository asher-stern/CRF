package com.asher_stern.crf.crf;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.asher_stern.crf.utilities.CrfException;
import static com.asher_stern.crf.utilities.ArithmeticUtilities.*;

/**
 * Implementation of the Viterbi algorithm.
 * <BR>
 * The Viterbi algorithm finds the most probable sequence of tags for a given sentence (sequence) under the given model.
 * <BR>
 * The algorithm works as follows:<BR>
 * Let \delta_j(g) denote the probability of the most probable sequence of tags from 0 to j, which ends with the tag g.<BR>
 * Consequently, \delta_l(g), where l is the "sentence-length -1" (in Java all arrays start with index 0), is the most
 * probable sequence of tags for the whole sentence, where the last tag is g.<BR>
 * Now, find the tag g which maximizes \delta_l(g), and you get the probability of the most probable sequence of tags for
 * the whole sequence.
 * Moreover, that "g" (the one which maximizes \delta_l(g)) is the tag of the last token in the sequence.
 * <BR>
 * The formula to calculate \delta_j(g) is:<BR>
 * \delta_j(g) = max_{g'}{\delta_{j-1}(g')*\psi_j(g,g')}<BR>
 * where \psi_j(g,g') is the formula for token number j in the sequence, where its tag is g, and the tag of token number j-1 is g'.<BR>
 * This formula, in CRF, is e^{\sum_{i=0}^{k-1}{\theta_i*f_i(sequecne,j,g,g')}}/Z(sequence)<BR>
 * where k is the number of features, theta_i is the parameter number i, and f_i is feature number i,
 * and Z(sequence) is the normalization factor.
 * <P>
 * Now, it can be observed that to calculate \delta_j(g), the tag g' should be detected, and that g' is the tag assigned to
 * token j-1, when it is needed to maximize a sequence of tags for [0..j] that ends with g.<BR>
 * Thus, during the computation of \delta_j(g) the algorithm keeps track of argmax_j(g) = g'.
 * In other words, the algorithm "remembers" for each j and g what is the tag g' that should be assigned to token j-1.
 * <P>
 * When the algorithm ends, the tag g for token "sentence-length-1" is known (see above).
 * Using argmax_j(g) it is possible to find the tag g' for "sentence-length-2". In the same way, g'' for "sentence-length-3"
 * can be found, until the first token of the sentence.
 * 
 * 
 *  
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfInferenceViterbi<K, G>
{
	/**
	 * Constructs Viterbi implementation for the given sentence, under the given model.
	 * @param model
	 * @param sentence
	 */
	public CrfInferenceViterbi(CrfModel<K, G> model, K[] sentence)
	{
		this.model = model;
		this.sentence = sentence;
	}

	/**
	 * Finds and returns the most probable sequence of tags for the sentence given in the constructor.
	 */
	public G[] inferBestTagSequence()
	{
		calculateViterbi();
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	private void calculateViterbi()
	{
		delta_viterbiForward = (LinkedHashMap<G, BigDecimal>[]) new LinkedHashMap[sentence.length];
		argmaxTags = (LinkedHashMap<G, G>[]) new LinkedHashMap[sentence.length];
		for (int i=0;i<argmaxTags.length;++i){argmaxTags[i]=null;}
		
		for (int index=0;index<sentence.length;++index)
		{
			Map<G, BigDecimal> delta_viterbiForwardCurrentToken = new LinkedHashMap<G, BigDecimal>();
			argmaxTags[index] = new LinkedHashMap<G, G>();
			for (G tag : model.getCrfTags().getTags())
			{
				Set<G> tagsOfPrevious = null; // The set of tags that can be assigned to token index-1.
				if (0==index) {tagsOfPrevious=Collections.singleton(null);}
				else {tagsOfPrevious=delta_viterbiForward[index-1].keySet();}
				BigDecimal maxValueByPrevious = null;
				G tagOfPreviousWithMaxValue = null;
				for (G tagOfPrevious : tagsOfPrevious)
				{
					BigDecimal crfFormulaValue = CrfUtilities.oneTokenFormula(model,sentence,index,tag,tagOfPrevious);
					BigDecimal valueByPrevious = crfFormulaValue;
					if (index>0)
					{
						valueByPrevious = safeMultiply(valueByPrevious, delta_viterbiForward[index-1].get(tagOfPrevious));
					}

					boolean maxSoFarDetected = false;
					if (null==maxValueByPrevious) {maxSoFarDetected=true;}
					else if (maxValueByPrevious.compareTo(valueByPrevious) < 0) {maxSoFarDetected=true;}
					
					if (maxSoFarDetected)
					{
						maxValueByPrevious=valueByPrevious;
						tagOfPreviousWithMaxValue=tagOfPrevious;
					}
				} // end for-each previous-tag
				argmaxTags[index].put(tag,tagOfPreviousWithMaxValue); // i.e. If the tag for token number "index" is "tag", then the tag for token "index-1" is "tagOfPreviousWithMaxValue". 
				delta_viterbiForwardCurrentToken.put(tag,maxValueByPrevious);
			} // end for-each current-tag
			delta_viterbiForward[index]=delta_viterbiForwardCurrentToken;
		} // end for-each token-in-sentence

		G tagOfLastToken = getArgMax(delta_viterbiForward[sentence.length-1]);
		
		result = (G[]) Array.newInstance(tagOfLastToken.getClass(), sentence.length); // new G[sentence.length];
		G bestTagCurrentIndex = tagOfLastToken;
		for (int tokenIndex=sentence.length-1;tokenIndex>=0;--tokenIndex)
		{
			result[tokenIndex] = bestTagCurrentIndex;
			bestTagCurrentIndex = argmaxTags[tokenIndex].get(bestTagCurrentIndex);
		}
		if (bestTagCurrentIndex!=null) {throw new CrfException("BUG");} // the tag of "before the first token" must be null.
		
		// Sanity checks
		if (result.length!=sentence.length) throw new CrfException("BUG: assignment array has different length than the sentence.");
		for (int i=0;i<result.length;++i)
		{
			if (null==argmaxTags[i]) {throw new CrfException("BUG: null tag assigned to token: "+i);}
		}
	}

	
	
	private G getArgMax(Map<G, BigDecimal> delta_oneTokenViterbiForward)
	{
		BigDecimal maxValueForLastToken = null;
		G tagWithMaxValueForLastToken = null;
		for (G tag : model.getCrfTags().getTags())
		{
			BigDecimal value = delta_oneTokenViterbiForward.get(tag);
			
			boolean maxDetected = false;
			if (null==maxValueForLastToken) {maxDetected=true;}
			else if (value.compareTo(maxValueForLastToken) > 0) {maxDetected=true;}
			
			if (maxDetected)
			{
				maxValueForLastToken=value;
				tagWithMaxValueForLastToken=tag;
			}
		}
		return tagWithMaxValueForLastToken;
	}
	
	
	private final CrfModel<K, G> model;
	private final K[] sentence;
	
	
	/**
	 * This is \delta_j(g). delta_viterbiForward[j].get(g) is the probability of the most
	 * probable sequence of tags from 0 to j, where the tag for token j is g.
	 */
	private Map<G, BigDecimal>[] delta_viterbiForward = null; // the map must permit null keys
	
	/**
	 * argmaxTags[j].get(g) is the tag g', which is the tag for token j-1 in the most probable sequence of tags from 0 to j
	 * where the tag for j is g.
	 */
	private Map<G, G>[] argmaxTags = null; // Map from current tag to previous tag.
	
	/**
	 * The most probable sequence of tags for the given sentence.
	 */
	private G[] result = null;
}
