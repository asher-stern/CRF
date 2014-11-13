package org.postagging.crf;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.postagging.crf.features.CrfFeaturesAndFilters;

/**
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfRememberActiveFeatures<K, G>
{
	public static <K, G> CrfRememberActiveFeatures<K, G> findForSentence(CrfFeaturesAndFilters<K, G> features, Set<G> tags, K[] sentence)
	{
		CrfRememberActiveFeatures<K, G> ret = new CrfRememberActiveFeatures<K, G>(features,tags,sentence);
		ret.findActiveFeaturesForAllTokens();
		return ret;
	}
	
	@SuppressWarnings("unchecked")
	public CrfRememberActiveFeatures(CrfFeaturesAndFilters<K, G> features, Set<G> tags, K[] sentence)
	{
		super();
		this.features = features;
		this.tags = tags;
		this.sentence = sentence;
		allTokensAndTagsActiveFeatures = (Map<G, Map<G, Set<Integer> >>[]) new Map[sentence.length];
	}



	public void findActiveFeaturesForAllTokens()
	{
		for (int tokenIndex=0;tokenIndex<sentence.length;++tokenIndex)
		{
			for (G currentTag : tags)
			{
				Set<G> possiblePreviousTags = null;
				if (tokenIndex>0) {possiblePreviousTags=tags;}
				else {possiblePreviousTags=Collections.singleton(null);}
				for (G previousTag : possiblePreviousTags)
				{
					Set<Integer> activeFeatures = CrfUtilities.getActiveFeatureIndexes(features,sentence,tokenIndex,currentTag,previousTag);
					putActiveFeatures(tokenIndex,currentTag,previousTag,activeFeatures);
				}
			}
		}
	}


	
	public Set<Integer> getOneTokenActiveFeatures(int tokenIndex, G currentTag, G previousTag)
	{
		return allTokensAndTagsActiveFeatures[tokenIndex].get(currentTag).get(previousTag);
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
	
	
	private final Set<G> tags;
	private final CrfFeaturesAndFilters<K, G> features;
	private final K[] sentence;

	private Map<G, Map<G, Set<Integer> >>[] allTokensAndTagsActiveFeatures;
}
