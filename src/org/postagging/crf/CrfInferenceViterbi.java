package org.postagging.crf;

import java.lang.reflect.Array;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.postagging.utilities.PosTaggerException;

/**
 * Implementation of the Viterbi algorithm.
 * <BR>
 * The Viterbi algorithm finds the most probable sequence of tags for a given sentence (sequence) under the given model.
 * <BR>
 * The algorithm works as follows:<BR>
 * Let \delta_j(s) denote the probability of the most probable sequence of tags from 0 to j, which ends with the tag s.<BR>
 * Consequently, \delta_l(s), where l is the "sentence-length -1" (in Java all arrays start with index 0), is the most
 * probable sequence of tags for the whole sentence, where the last tag is s.<BR>
 * Now, find the tag s which maximizes \delta_l(s), and you get the probability of the most probable sequence of tags for
 * the whole sequence.
 * Moreover, that "s" (the one which maximizes \delta_l(s)) is the tag of the last token in the sequence.
 * <BR>
 * The formula to calculate \delta_j(s) is:<BR>
 * \delta_j(s) = max_{s'}{\delta_{j-1}(s')*\psi_j(s,s')}<BR>
 * where \psi_j(s,s') is the formula for token number j in the sequence, where its tag is s, and the tag of token number j-1 is s'.<BR>
 * This formula, in CRF, is e^{\sum_{i=0}^{k-1}{\theta_i*f_i(sequecne,j,s,s')}}/Z(sequence)<BR>
 * where k is the number of features, theta_i is the parameter number i, and f_i is feature number i,
 * and Z(sequence) is the normalization factor.
 * <P>
 * Now, it can be observed that to calculate \delta_j(s), the tag s' should be detected, and that s' is the tag assigned to
 * token j-1, when it is needed to maximize a sequence of tags for [0..j] that ends with s.<BR>
 * Thus, during the computation of \delta_j(s) the algorithm keeps track of argmax_j(s) = s'.
 * In other words, the algorithm "remembers" for each j and s what is the tag s' that should be assigned to token j-1.
 * <P>
 * When the algorithm ends, the tag s for token "sentence-length-1" is known (see above).
 * Using argmax_j(s) it is possible to find the tag s' for "sentence-length-2". In the same way, s'' for "sentence-length-3"
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
public class CrfInferenceViterbi<K, G> extends CrfInference<K, G>
{
	/**
	 * Constructs Viterbi implementation for the given sentence, under the given model.
	 * @param model
	 * @param sentence
	 */
	public CrfInferenceViterbi(CrfModel<K, G> model, K[] sentence)
	{
		super(model, sentence);
	}

	/*
	 * (non-Javadoc)
	 * @see org.postagging.crf.CrfInference#inferBestTagSequence()
	 */
	@Override
	public G[] inferBestTagSequence()
	{
		calculateViterbi();
		return result;
	}
	
	
	@SuppressWarnings("unchecked")
	private void calculateViterbi()
	{
		delta_viterbiForward = (LinkedHashMap<G, Double>[]) new LinkedHashMap[sentence.length];
		argmaxTags = (LinkedHashMap<G, G>[]) new LinkedHashMap[sentence.length];
		for (int i=0;i<argmaxTags.length;++i){argmaxTags[i]=null;}
		
		for (int index=0;index<sentence.length;++index)
		{
			Map<G, Double> delta_viterbiForwardCurrentToken = new LinkedHashMap<G, Double>();
			argmaxTags[index] = new LinkedHashMap<G, G>();
			for (G tag : model.getCrfTags().getTags())
			{
				Set<G> tagsOfPrevious = null; // The set of tags that can be assigned to token index-1.
				if (0==index) {tagsOfPrevious=Collections.singleton(null);}
				else {tagsOfPrevious=delta_viterbiForward[index-1].keySet();}
				Double maxValueByPrevious = null;
				G tagOfPreviousWithMaxValue = null;
				for (G tagOfPrevious : tagsOfPrevious)
				{
					double valueByPrevious = 0.0;
					if (model.getCrfTags().getCanPrecede().get(tag).contains(tagOfPrevious)) // did the sequence "tagOfPrevious" "tag" has ever been seen in the training corpus? If yes, calculate its probability. If no, the probability is 0.
					{
						double crfFormulaValue = CrfUtilities.oneTokenFormula(model,sentence,index,tag,tagOfPrevious);
						valueByPrevious = crfFormulaValue;
						if (index>0)
						{
							valueByPrevious = valueByPrevious*delta_viterbiForward[index-1].get(tagOfPrevious);
						}
					}

					boolean maxSoFarDetected = false;
					if (null==maxValueByPrevious) {maxSoFarDetected=true;}
					else if (maxValueByPrevious<valueByPrevious) {maxSoFarDetected=true;}
					
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
		if (bestTagCurrentIndex!=null) {throw new PosTaggerException("BUG");} // the tag of "before the first token" must be null.
		
		// Sanity checks
		if (result.length!=sentence.length) throw new PosTaggerException("BUG: assignment array has different length than the sentence.");
		for (int i=0;i<result.length;++i)
		{
			if (null==argmaxTags[i]) {throw new PosTaggerException("BUG: null tag assigned to token: "+i);}
		}
	}

	
	
	private G getArgMax(Map<G, Double> delta_oneTokenViterbiForward)
	{
		Double maxValueForLastToken = null;
		G tagWithMaxValueForLastToken = null;
		for (G tag : model.getCrfTags().getTags())
		{
			double value = delta_oneTokenViterbiForward.get(tag);
			
			boolean maxDetected = false;
			if (null==maxValueForLastToken) {maxDetected=true;}
			else if (value>maxValueForLastToken) {maxDetected=true;}
			
			if (maxDetected)
			{
				maxValueForLastToken=value;
				tagWithMaxValueForLastToken=tag;
			}
		}
		return tagWithMaxValueForLastToken;
	}
	
	
	/**
	 * This is \delta_j(s). delta_viterbiForward[j].get(s) is the probability of the most
	 * probable sequence of tags from 0 to j, where the tag for token j is s.
	 */
	private Map<G, Double>[] delta_viterbiForward = null; // the map must permit null keys
	
	/**
	 * argmaxTags[j].get(s) is the tag s', which is the tag for token j-1 in the most probable sequence of tags from 0 to j
	 * where the tag for j is s.
	 */
	private Map<G, G>[] argmaxTags = null; // Map from current tag to previous tag.
	
	/**
	 * The most probable sequence of tags for the given sentence.
	 */
	private G[] result = null;
}
