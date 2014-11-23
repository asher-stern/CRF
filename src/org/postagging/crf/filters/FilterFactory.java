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
 * @param <K>
 * @param <G>
 */
public interface FilterFactory<K, G> extends Serializable
{
	public Set<Filter<K, G>> createFilters(K[] sequence, int tokenIndex, G currentTag, G previousTag);
}
