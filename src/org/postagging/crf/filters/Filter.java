package org.postagging.crf.filters;

import java.io.Serializable;

import org.postagging.crf.CrfUtilities;

/**
 * A filter determines whether it is known <B>a priori</B> that the feature value is zero
 * for a given input.
 * <P>
 * The CRF formula e^{weight-vector*feature-vector} might be quite expensive in run-time complexity notion. However,
 * in most cases most of the features return zero for the given input. Thus, there is no need to sum over all the features,
 * but only over those which <B>might</B> return a non-zero value.
 * <BR>
 * Concretely, Assume we have a {@link CrfFilteredFeature} and a {@link Filter}: if the filter ({@link #equals(Object)}
 * to {@link CrfFilteredFeature#getFilter()}, then the feature <B>might</B> return a non-zero value. Otherwise, it is
 * known for sure that it returns zero.
 * <P>
 * The usage of filters is to build a map from {@link Filter} to a <B>set of</B> {@link CrfFilteredFeature}s, and for each input
 * create filters and use the to retrieve the set of relevant features from the map.   
 * 
 * @see CrfFilteredFeature
 * @see FilterFactory
 * @see CrfUtilities#getActiveFeatureIndexes(CrfFeaturesAndFilters, Object[], int, Object, Object)
 * 
 * @author Asher Stern
 * Date: November 2014
 *
 * @param <K>
 * @param <G>
 */
public abstract class Filter<K,G> implements Serializable
{
	private static final long serialVersionUID = 5563671313834518710L;
	
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
