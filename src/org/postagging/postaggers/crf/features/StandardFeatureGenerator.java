package org.postagging.postaggers.crf.features;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.postagging.crf.CrfUtilities;
import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.crf.features.Filter;
import org.postagging.crf.features.TokenAndTagFilter;
import org.postagging.crf.features.TwoTagsFilter;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.postaggers.crf.CrfPosTaggerFeatureGenerator;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class StandardFeatureGenerator extends CrfPosTaggerFeatureGenerator
{

	public StandardFeatureGenerator(InMemoryPosTagCorpus<String, String> corpus, Set<String> tags)
	{
		super(corpus, tags);
	}

	@Override
	public void generateFeatures()
	{
		setFilteredFeatures = new LinkedHashSet<CrfFilteredFeature<String,String>>();
		addTokenAndTagFeatures();
		addTagTransitionFeatures();
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
		
		allFeatures = new CrfFeaturesAndFilters<String, String>(
				new StandardFilterFactory(),
				featuresAsArray,
				mapActiveFeatures,
				indexesOfFeaturesWithNoFilter
				);
	}
	
	public CrfFeaturesAndFilters<String, String> getFeatures()
	{
		if (null==allFeatures) {throw new PosTaggerException("Features were not generated.");}
		return allFeatures;
	}
	
	
	
	
	private void addTokenAndTagFeatures()
	{
		for (List<? extends TaggedToken<String, String> > sentence : corpus)
		{
			for (TaggedToken<String, String> taggedToken : sentence)
			{
				setFilteredFeatures.add(
						new CrfFilteredFeature<String, String>(
								new TokenAndTagFeature(taggedToken.getToken(),taggedToken.getTag()),
								new TokenAndTagFilter<String, String>(taggedToken.getToken(),taggedToken.getTag(), null),
								true
								)
						);
			}
		}
	}
	
	private void addTagTransitionFeatures()
	{
		for (String tag : tags)
		{
			setFilteredFeatures.add(
					new CrfFilteredFeature<String,String>(
							new TagTransitionFeature(null, tag),
							new TwoTagsFilter<String, String>(null, tag, null),
							true)
					);
			
			for (String previousTag : tags)
			{
				setFilteredFeatures.add(
						new CrfFilteredFeature<String,String>(
								new TagTransitionFeature(previousTag, tag),
								new TwoTagsFilter<String, String>(null, tag, previousTag),
								true)
						);

			}
		}
	}
	
	

	protected Set<CrfFilteredFeature<String, String>> setFilteredFeatures = null;
	protected CrfFeaturesAndFilters<String, String> allFeatures = null;
}
