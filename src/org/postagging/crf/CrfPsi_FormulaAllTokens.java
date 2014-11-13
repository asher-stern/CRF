package org.postagging.crf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfPsi_FormulaAllTokens<K,G>
{
	public static <K,G> CrfPsi_FormulaAllTokens<K,G> createAndCalculate(CrfModel<K, G> model, K[] sentence)
	{
		CrfPsi_FormulaAllTokens<K,G> ret = new CrfPsi_FormulaAllTokens<K,G>(model,sentence);
		ret.calculateFormulasForAllTokens();
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public CrfPsi_FormulaAllTokens(CrfModel<K, G> model, K[] sentence)
	{
		super();
		this.model = model;
		this.sentence = sentence;
		this.allPsiValues = (Map<G, Map<G, Double>>[]) new Map[sentence.length];
		allTokensAndTagsActiveFeatures = (Map<G, Map<G, Set<Integer> >>[]) new Map[sentence.length];
	}
	
	
	public void calculateFormulasForAllTokens()
	{
		for (int tokenIndex=0;tokenIndex<sentence.length;++tokenIndex)
		{
			for (G currentTag : model.getTags())
			{
				Set<G> possiblePreviousTags = null;
				if (tokenIndex>0) {possiblePreviousTags=model.getTags();}
				else {possiblePreviousTags=Collections.singleton(null);}
				for (G previousTag : possiblePreviousTags)
				{
					Set<Integer> activeFeatures = CrfUtilities.getActiveFeatureIndexes(model.getFeatures(),sentence,tokenIndex,currentTag,previousTag);
					putActiveFeatures(tokenIndex,currentTag,previousTag,activeFeatures);
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
	
	public Set<Integer> getOneTokenActiveFeatures(int tokenIndex, G currentTag, G previousTag)
	{
		return allTokensAndTagsActiveFeatures[tokenIndex].get(currentTag).get(previousTag);
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

	private void putActiveFeatures(int tokenIndex, G currentTag, G previousTag, Set<Integer> activeFeatures)
	{
		Map<G, Map<G, Set<Integer> >> mapForToken = allTokensAndTagsActiveFeatures[tokenIndex];
		if (null==mapForToken)
		{
			mapForToken = new LinkedHashMap<G, Map<G, Set<Integer> >>();
			allTokensAndTagsActiveFeatures[tokenIndex] = mapForToken;
		}
		
		Map<G, Set<Integer> > mapForCurrentTag = mapForToken.get(currentTag);
		if (null==mapForCurrentTag)
		{
			mapForCurrentTag = new LinkedHashMap<G, Set<Integer> >();
			mapForToken.put(currentTag, mapForCurrentTag);
		}
		
		mapForCurrentTag.put(previousTag, activeFeatures);
	}
	
	private final CrfModel<K,G> model;
	private final K[] sentence;
	
	private Map<G, Map<G, Double>>[] allPsiValues;
	private Map<G, Map<G, Set<Integer> >>[] allTokensAndTagsActiveFeatures;
}
