package org.postagging.crf.filters;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

/**
 * Encapsulates all the features, the {@link FilterFactory}, and data-structures used for filtering features.
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfFeaturesAndFilters<K, G> implements Serializable
{
	private static final long serialVersionUID = 5815029007668803039L;
	
	public CrfFeaturesAndFilters(FilterFactory<K, G> filterFactory,
			CrfFilteredFeature<K, G>[] filteredFeatures,
			Map<Filter<K, G>, Set<Integer>> mapActiveFeatures,
			Set<Integer> indexesOfFeaturesWithNoFilter)
	{
		super();
		this.filterFactory = filterFactory;
		this.filteredFeatures = filteredFeatures;
		this.mapActiveFeatures = mapActiveFeatures;
		this.indexesOfFeaturesWithNoFilter = indexesOfFeaturesWithNoFilter;
	}
	
	
	
	public FilterFactory<K, G> getFilterFactory()
	{
		return filterFactory;
	}
	public CrfFilteredFeature<K, G>[] getFilteredFeatures()
	{
		return filteredFeatures;
	}
	public Map<Filter<K, G>, Set<Integer>> getMapActiveFeatures()
	{
		return mapActiveFeatures;
	}
	public Set<Integer> getIndexesOfFeaturesWithNoFilter()
	{
		return indexesOfFeaturesWithNoFilter;
	}




	private final FilterFactory<K, G> filterFactory;
	private final CrfFilteredFeature<K, G>[] filteredFeatures;
	private final Map<Filter<K, G>, Set<Integer>> mapActiveFeatures;
	private final Set<Integer> indexesOfFeaturesWithNoFilter;
}
