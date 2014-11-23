package org.postagging.crf.filters;

import java.io.Serializable;
import java.util.Set;

import org.postagging.crf.CrfUtilities;

/**
 * Creates a set of filters for the given input. "Input" is the sequence of tokens, the token-index, its tag, and the tag of the preceding token.
 * 
 * @see Filter
 * @see CrfFilteredFeature
 * @see CrfUtilities#getActiveFeatureIndexes(CrfFeaturesAndFilters, Object[], int, Object, Object)
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K> type of tokens
 * @param <G> type of tags
 */
public interface FilterFactory<K, G> extends Serializable
{
	/**
	 * Creates a set of filters for the given token,tag, and tag-of-previous-token.
	 * The convention is as follows:
	 * Let each feature f be a feature that <B>might</B> return non-zero for the given token,tag,previous-tag.
	 * That feature is encapsulated with a {@link Filter} in a {@link CrfFilteredFeature}. Let's call this filter "t".
	 * For that filter there exist one filter in the set returned by this function, name it "t'", such that "t'" equals to "t".
	 * 
	 * 
	 * @param sequence A sequence of tokens
	 * @param tokenIndex An index of a token in that sequence
	 * @param currentTag A tag for that token
	 * @param previousTag A tag for the token which immediately precedes that token.
	 * @return A set of filters as described above.
	 */
	public Set<Filter<K, G>> createFilters(K[] sequence, int tokenIndex, G currentTag, G previousTag);
}
