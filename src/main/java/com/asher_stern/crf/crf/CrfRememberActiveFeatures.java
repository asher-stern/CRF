package com.asher_stern.crf.crf;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.asher_stern.crf.crf.filters.CrfFeaturesAndFilters;

/**
 * Holds sets of active features for every token, and every pair of tags (for this token and the preceding token) in the given input.
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfRememberActiveFeatures<K, G>
{
	/**
	 * Creates an instance of {@link CrfRememberActiveFeatures} for the given sentence, calls its {@link #findActiveFeaturesForAllTokens()}
	 * method, and returns it. With the returned object, the method {@link #getOneTokenActiveFeatures(int, Object, Object)} can be
	 * used to retrieve active features for any token/tag/tag-of-previous in the sentence.
	 * 
	 * @param features
	 * @param crfTags
	 * @param sentence
	 * @return
	 */
	public static <K, G> CrfRememberActiveFeatures<K, G> findForSentence(CrfFeaturesAndFilters<K, G> features, CrfTags<G> crfTags, K[] sentence)
	{
		CrfRememberActiveFeatures<K, G> ret = new CrfRememberActiveFeatures<K, G>(features,crfTags,sentence);
		ret.findActiveFeaturesForAllTokens();
		return ret;
	}
	
	/**
	 * Constructor for a given sentence.
	 * 
	 * @param features
	 * @param crfTags
	 * @param sentence
	 */
	@SuppressWarnings("unchecked")
	public CrfRememberActiveFeatures(CrfFeaturesAndFilters<K, G> features, CrfTags<G> crfTags, K[] sentence)
	{
		super();
		this.features = features;
		this.crfTags = crfTags;
		this.sentence = sentence;
		allTokensAndTagsActiveFeatures = (Map<G, Map<G, Set<Integer> >>[]) new Map[sentence.length];
	}



	/**
	 * Finds all the active features for every triple of token/tag/tag-of-previous. Then, the method
	 * {@link #getOneTokenActiveFeatures(int, Object, Object)} can be used.
	 */
	public void findActiveFeaturesForAllTokens()
	{
		for (int tokenIndex=0;tokenIndex<sentence.length;++tokenIndex)
		{
			for (G currentTag : crfTags.getTags())
			{
				Set<G> possiblePreviousTags = CrfUtilities.getPreviousTags(sentence, tokenIndex, currentTag, crfTags);
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
	
	
	private final CrfTags<G> crfTags;
	private final CrfFeaturesAndFilters<K, G> features;
	private final K[] sentence;

	private Map<G, Map<G, Set<Integer> >>[] allTokensAndTagsActiveFeatures;
}
