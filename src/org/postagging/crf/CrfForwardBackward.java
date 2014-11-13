package org.postagging.crf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.postagging.utilities.ArbitraryRangeArray;
import org.postagging.utilities.PosTaggerException;

import static org.postagging.crf.CrfUtilities.relativeDifference;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfForwardBackward<K,G>
{
	public static final double DEBUG_ALLOWED_DIFFERENCE_BETWEEN_FINAL_ALPHA_AND_FINAL_BETA = 0.01;
	
	public CrfForwardBackward(CrfModel<K, G> model, K[] sentence)
	{
		super();
		this.model = model;
		this.sentence = sentence;
	}
	
	public void calculateForwardAndBackward()
	{
		calculateAlphaForward();
		calculateBetaBackward();
		
		if ((relativeDifference(finalAlpha,finalBeta)-1.0)>DEBUG_ALLOWED_DIFFERENCE_BETWEEN_FINAL_ALPHA_AND_FINAL_BETA)
		{
			String errorMessage = "The calculated final-alpha and final-beta, both correspond to Z(x) (the normalization factor) differ.\n"
					+ "Z(x) by alpha (forward) = "+String.format("%-3.3f", finalAlpha)+". Z(x) by beta (backward) = "+String.format("%-3.3f", finalBeta);
			throw new PosTaggerException(errorMessage);
			//logger.error(errorMessage);
		}
		calculated=true;
	}
	
	public void calculateOnlyNormalizationFactor()
	{
		calculateAlphaForward();
		onlyNormalizationFactorCalculated = true;
	}
	
	
	
	public Map<G, Double>[] getAlpha_forward()
	{
		if (!calculated) {throw new PosTaggerException("forward-backward not calculated");}
		return alpha_forward;
	}

	public ArbitraryRangeArray<LinkedHashMap<G, Double>> getBeta_backward()
	{
		if (!calculated) {throw new PosTaggerException("forward-backward not calculated");}
		return beta_backward;
	}
	
	public double getCalculatedNormalizationFactor()
	{
		if ( (!calculated) && (!onlyNormalizationFactorCalculated) ) {throw new PosTaggerException("forward-backward not calculated");}
		return finalAlpha;
	}

	
	
	@SuppressWarnings("unchecked")
	private void calculateAlphaForward()
	{
		alpha_forward = (LinkedHashMap<G, Double>[]) new LinkedHashMap[sentence.length];
		for (int index=0;index<sentence.length;++index)
		{
			Map<G, Double> alpha_forwardThisToken = new LinkedHashMap<G, Double>();
			for (G tag : model.getTags())
			{
				Set<G> previousTags = null;
				if (index==0) {previousTags = Collections.singleton(null);}
				else {previousTags = alpha_forward[index-1].keySet();}
				double sumOverPreviousTags = 0.0;
				for (G previousTag : previousTags)
				{
					double valueForPreviousTag = CrfUtilities.oneTokenFormula(model,sentence,index,tag,previousTag);
					if (index>0)
					{
						double previousAlphaValue = alpha_forward[index-1].get(previousTag);
						valueForPreviousTag = valueForPreviousTag*previousAlphaValue;
					}
					sumOverPreviousTags += valueForPreviousTag;
				}
				alpha_forwardThisToken.put(tag, sumOverPreviousTags);
			}
			alpha_forward[index] = alpha_forwardThisToken;
		}
		
		
		finalAlpha = 0.0;
		Map<G,Double> alphaLast = alpha_forward[sentence.length-1];
		for (G tag : alphaLast.keySet())
		{
			finalAlpha += alphaLast.get(tag);
		}
	}
	
	
	
	private void calculateBetaBackward()
	{
		beta_backward = new ArbitraryRangeArray<LinkedHashMap<G, Double>>(sentence.length+1, -1); // i.e. [-1,0,1,2,...,sentence.length-1]
		beta_backward.set(sentence.length-1,new LinkedHashMap<G, Double>());
		for (G tag : model.getTags())
		{
			beta_backward.get(sentence.length-1).put(tag, 1.0);
		}
		
		for (int index=sentence.length-2;index>=(-1);--index)
		{
			LinkedHashMap<G, Double> betaCurrentToken = new LinkedHashMap<G, Double>();
			
			Set<G> currentTokenPossibleTags = null;
			if (index<0) {currentTokenPossibleTags=Collections.singleton(null);}
			else {currentTokenPossibleTags = model.getTags();}
			for (G tag : currentTokenPossibleTags)
			{
				double sum = 0.0;
				for (G nextTag : beta_backward.get(index+1).keySet())
				{
					double valueCurrentTokenCrfFormula = CrfUtilities.oneTokenFormula(model,sentence,index+1,nextTag,tag);
					double valueForNextTag = valueCurrentTokenCrfFormula*beta_backward.get(index+1).get(nextTag);
					sum += valueForNextTag;
				}
				betaCurrentToken.put(tag,sum);
			}
			beta_backward.set(index, betaCurrentToken);
		}
		
		finalBeta = beta_backward.get(-1).get(null);
	}
	
	
	
	
	protected final CrfModel<K, G> model;
	protected final K[] sentence;
	
	private Map<G, Double>[] alpha_forward;
	private ArbitraryRangeArray<LinkedHashMap<G, Double>> beta_backward;
	private double finalAlpha = 0.0;
	private double finalBeta = 0.0;
	
	private boolean calculated = false;
	private boolean onlyNormalizationFactorCalculated = false;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CrfForwardBackward.class);
}
