package org.postagging.postaggers.crf;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.postagging.crf.CrfUtilities;
import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.crf.features.Filter;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.postaggers.crf.features.StandardFeatureGenerator;
import org.postagging.postaggers.crf.features.StandardFilterFactory;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.PosTaggerUtilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class CrfPosTaggerTrainerFactory
{
	public CrfPosTaggerTrainer createPosTaggerTrainer(InMemoryPosTagCorpus<String, String> corpus)
	{
		return createPosTaggerTrainer(corpus,
				(InMemoryPosTagCorpus<String, String> theCorpus, Set<String> tags) -> new StandardFeatureGenerator(theCorpus, tags)
		);

		
	}
	
	public CrfPosTaggerTrainer createPosTaggerTrainer(InMemoryPosTagCorpus<String, String> corpus, CrfPosTaggerFeatureGeneratorFactory featureGeneratorFactory)
	{
		logger.info("Extracting tags.");
		Set<String> tags = PosTaggerUtilities.extractAllTagsFromCorpus(corpus);
		logger.info("Generating features.");
		CrfPosTaggerFeatureGenerator featureGenerator = featureGeneratorFactory.create(corpus, tags);
		featureGenerator.generateFeatures();
		Set<CrfFilteredFeature<String, String>> setFilteredFeatures = featureGenerator.getFeatures();
		CrfFeaturesAndFilters<String, String> features = createFeaturesAndFiltersObjectFromSetOfFeatures(setFilteredFeatures);
		
		logger.info("CrfPosTaggerTrainer has been created.");
		return new CrfPosTaggerTrainer(features,tags);
	}
	
	
	
	
	private static CrfFeaturesAndFilters<String, String> createFeaturesAndFiltersObjectFromSetOfFeatures(Set<CrfFilteredFeature<String, String>> setFilteredFeatures)
	{
		if (setFilteredFeatures.size()<=0) throw new PosTaggerException("No features have been generated.");
		@SuppressWarnings("unchecked")
		CrfFilteredFeature<String,String>[] featuresAsArray = (CrfFilteredFeature<String,String>[]) Array.newInstance(setFilteredFeatures.iterator().next().getClass(), setFilteredFeatures.size()); // new CrfFilteredFeature<String,String>[setFilteredFeatures.size()];
		Iterator<CrfFilteredFeature<String,String>> filteredFeatureIterator = setFilteredFeatures.iterator();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			if (!filteredFeatureIterator.hasNext()) {throw new PosTaggerException("BUG");}
			CrfFilteredFeature<String,String> filteredFeature = filteredFeatureIterator.next();
			featuresAsArray[index] = filteredFeature;
		}
		if (filteredFeatureIterator.hasNext()) {throw new PosTaggerException("BUG");}
		
		
		Set<Integer> indexesOfFeaturesWithNoFilter = new LinkedHashSet<Integer>();
		Map<Filter<String, String>, Set<Integer>> mapActiveFeatures = new LinkedHashMap<Filter<String,String>, Set<Integer>>();
		for (int index=0;index<featuresAsArray.length;++index)
		{
			CrfFilteredFeature<String,String> filteredFeature = featuresAsArray[index];
			Filter<String, String> filter = filteredFeature.getFilter();
			if (filter!=null)
			{
				CrfUtilities.putInMapSet(mapActiveFeatures, filter, index);
			}
			else
			{
				indexesOfFeaturesWithNoFilter.add(index);
			}
		}
		
		CrfFeaturesAndFilters<String, String> allFeatures = new CrfFeaturesAndFilters<String, String>(
				new StandardFilterFactory(),
				featuresAsArray,
				mapActiveFeatures,
				indexesOfFeaturesWithNoFilter
				);
		
		return allFeatures;
	}
	

	private static final Logger logger = Logger.getLogger(CrfPosTaggerTrainerFactory.class);
}
