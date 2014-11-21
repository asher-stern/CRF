package org.postagging.crf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * Holds, for a given sentence, the CRF formula value for each token-index and every pair of tags (for the current token and the
 * preceding token).
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfPsi_FormulaAllTokens<K,G>
{
	public static <K,G> CrfPsi_FormulaAllTokens<K,G> createAndCalculate(CrfModel<K, G> model, K[] sentence, CrfRememberActiveFeatures<K, G> activeFeaturesForSentence)
	{
		CrfPsi_FormulaAllTokens<K,G> ret = new CrfPsi_FormulaAllTokens<K,G>(model,sentence,activeFeaturesForSentence);
		ret.calculateFormulasForAllTokens();
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public CrfPsi_FormulaAllTokens(CrfModel<K, G> model, K[] sentence, CrfRememberActiveFeatures<K, G> activeFeaturesForSentence)
	{
		super();
		this.model = model;
		this.sentence = sentence;
		this.activeFeaturesForSentence = activeFeaturesForSentence;
		this.allPsiValues = (Map<G, Map<G, Double>>[]) new Map[sentence.length];
	}
	
	
	public void calculateFormulasForAllTokens()
	{
		for (int tokenIndex=0;tokenIndex<sentence.length;++tokenIndex)
		{
			for (G currentTag : model.getCrfTags().getTags())
			{
				Set<G> possiblePreviousTags = CrfUtilities.getPreviousTags(sentence, tokenIndex, currentTag, model.getCrfTags());
				for (G previousTag : possiblePreviousTags)
				{
					Set<Integer> activeFeatures = activeFeaturesForSentence.getOneTokenActiveFeatures(tokenIndex, currentTag, previousTag);
					double value = CrfUtilities.oneTokenFormula(model,sentence,tokenIndex,currentTag,previousTag,activeFeatures);
					put(tokenIndex,currentTag,previousTag,value);
				}
			}
		}
	}


	public double getOneTokenFormula(int tokenIndex, G currentTag, G previousTag)
	{
		return allPsiValues[tokenIndex].get(currentTag).get(previousTag);
	}
	
	
	
	
	
	private void put(int tokenIndex, G currentTag, G previousTag, double value)
	{
		Map<G, Map<G, Double>> mapForToken = allPsiValues[tokenIndex];
		if (null==mapForToken)
		{
			mapForToken = new LinkedHashMap<G, Map<G,Double>>();
			allPsiValues[tokenIndex] = mapForToken;
		}
		
		Map<G, Double> mapForCurrentTag = mapForToken.get(currentTag);
		if (null==mapForCurrentTag)
		{
			mapForCurrentTag = new LinkedHashMap<G, Double>();
			mapForToken.put(currentTag, mapForCurrentTag);
		}
		
		mapForCurrentTag.put(previousTag, value);
	}

	
	private final CrfModel<K,G> model;
	private final K[] sentence;
	private final CrfRememberActiveFeatures<K, G> activeFeaturesForSentence;
	
	private Map<G, Map<G, Double>>[] allPsiValues;
}
