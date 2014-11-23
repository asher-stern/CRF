package org.postagging.crf.run;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.postagging.crf.CrfTags;
import org.postagging.crf.CrfUtilities;
import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.crf.features.Filter;
import org.postagging.crf.features.FilterFactory;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;


/**
 * 
 * @author Asher Stern
 * Date: Nov 23, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfTrainerFactory<K,G>
{
	public CrfTrainer<K,G> createTrainer(Iterable<List<? extends TaggedToken<K, G> >> corpus, CrfFeatureGeneratorFactory<K,G> featureGeneratorFactory, FilterFactory<K, G> filterFactory)
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
		if (setFilteredFeatures.size()<=0) throw new PosTaggerException("No features have been generated.");
		@SuppressWarnings("unchecked")
		CrfFilteredFeature<K,G>[] featuresAsArray = (CrfFilteredFeature<K,G>[]) Array.newInstance(setFilteredFeatures.iterator().next().getClass(), setFilteredFeatures.size()); // new CrfFilteredFeature<String,String>[setFilteredFeatures.size()];
		Iterator<CrfFilteredFeature<K,G>> filteredFeatureIterator = setFilteredFeatures.iterator();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			if (!filteredFeatureIterator.hasNext()) {throw new PosTaggerException("BUG");}
			CrfFilteredFeature<K,G> filteredFeature = filteredFeatureIterator.next();
			featuresAsArray[index] = filteredFeature;
		}
		if (filteredFeatureIterator.hasNext()) {throw new PosTaggerException("BUG");}
		
		
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
