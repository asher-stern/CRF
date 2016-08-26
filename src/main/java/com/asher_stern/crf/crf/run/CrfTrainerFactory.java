package com.asher_stern.crf.crf.run;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.asher_stern.crf.crf.CrfTags;
import com.asher_stern.crf.crf.CrfUtilities;
import com.asher_stern.crf.crf.filters.CrfFeaturesAndFilters;
import com.asher_stern.crf.crf.filters.CrfFilteredFeature;
import com.asher_stern.crf.crf.filters.Filter;
import com.asher_stern.crf.crf.filters.FilterFactory;
import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.TaggedToken;


/**
 * A factory which generates a new CRF trainer.
 * 
 * @author Asher Stern
 * Date: Nov 23, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfTrainerFactory<K,G>
{
	/**
	 * Creates a CRF trainer.<BR>
	 * <B>The given corpus must reside completely in the internal memory. Not in disk/data-base etc.</B>
	 * 
	 * @param corpus The corpus: a list a tagged sequences. Must reside completely in memory.
	 * @param featureGeneratorFactory A factory which creates a feature-generator (the feature-generator creates a set of features)
	 * @param filterFactory The {@link FilterFactory} <B>that corresponds to the feature-generator.</B>
	 * 
	 * @return a CRF trainer.
	 */
	public CrfTrainer<K,G> createTrainer(List<List<? extends TaggedToken<K, G> >> corpus, CrfFeatureGeneratorFactory<K,G> featureGeneratorFactory, FilterFactory<K, G> filterFactory)
	{
		logger.info("Extracting tags.");
		CrfTagsBuilder<G> tagsBuilder = new CrfTagsBuilder<G>(corpus);
		tagsBuilder.build();
		CrfTags<G> crfTags = tagsBuilder.getCrfTags();
		
		logger.info("Generating features.");
		CrfFeatureGenerator<K,G> featureGenerator = featureGeneratorFactory.create(corpus, crfTags.getTags());
		featureGenerator.generateFeatures();
		Set<CrfFilteredFeature<K, G>> setFilteredFeatures = featureGenerator.getFeatures();
		CrfFeaturesAndFilters<K, G> features = createFeaturesAndFiltersObjectFromSetOfFeatures(setFilteredFeatures, filterFactory);
		
		logger.info("CrfPosTaggerTrainer has been created.");
		return new CrfTrainer<K,G>(features,crfTags);
	}
	
	
	
	
	private static <K,G> CrfFeaturesAndFilters<K, G> createFeaturesAndFiltersObjectFromSetOfFeatures(Set<CrfFilteredFeature<K, G>> setFilteredFeatures, FilterFactory<K, G> filterFactory)
	{
		if (setFilteredFeatures.size()<=0) throw new CrfException("No features have been generated.");
		@SuppressWarnings("unchecked")
		CrfFilteredFeature<K,G>[] featuresAsArray = (CrfFilteredFeature<K,G>[]) Array.newInstance(setFilteredFeatures.iterator().next().getClass(), setFilteredFeatures.size()); // new CrfFilteredFeature<String,String>[setFilteredFeatures.size()];
		Iterator<CrfFilteredFeature<K,G>> filteredFeatureIterator = setFilteredFeatures.iterator();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			if (!filteredFeatureIterator.hasNext()) {throw new CrfException("BUG");}
			CrfFilteredFeature<K,G> filteredFeature = filteredFeatureIterator.next();
			featuresAsArray[index] = filteredFeature;
		}
		if (filteredFeatureIterator.hasNext()) {throw new CrfException("BUG");}
		
		
		Set<Integer> indexesOfFeaturesWithNoFilter = new LinkedHashSet<Integer>();
		Map<Filter<K, G>, Set<Integer>> mapActiveFeatures = new LinkedHashMap<Filter<K, G>, Set<Integer>>();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			CrfFilteredFeature<K, G> filteredFeature = featuresAsArray[index];
			Filter<K, G> filter = filteredFeature.getFilter();
			if (filter!=null)
			{
				CrfUtilities.putInMapSet(mapActiveFeatures, filter, index);
			}
			else
			{
				indexesOfFeaturesWithNoFilter.add(index);
			}
		}
		
		CrfFeaturesAndFilters<K, G> allFeatures = new CrfFeaturesAndFilters<K, G>(
				filterFactory,
				featuresAsArray,
				mapActiveFeatures,
				indexesOfFeaturesWithNoFilter
				);
		
		return allFeatures;
	}

	
	private static final Logger logger = Logger.getLogger(CrfTrainerFactory.class);
}
