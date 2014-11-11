package org.postagging.crf.features;

import java.util.Map;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfFeaturesAndFilters<K, G>
{
	public CrfFeaturesAndFilters(FilterFactory<K, G> filterFactory,
			CrfFilteredFeature<K, G>[] filteredFeatures,
			Map<Filter<K, G>, Set<Integer>> mapActiveFeatures)
	{
		super();
		this.filterFactory = filterFactory;
		this.filteredFeatures = filteredFeatures;
		this.mapActiveFeatures = mapActiveFeatures;
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



	private final FilterFactory<K, G> filterFactory;
	private final CrfFilteredFeature<K, G>[] filteredFeatures;
	private final Map<Filter<K, G>, Set<Integer>> mapActiveFeatures;
}
