package org.postagging.crf;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.utilities.TaggedToken;
import org.postagging.utilities.PosTaggerException;

import static org.postagging.crf.CrfUtilities.safeAdd;


/**
 * 
 * @author Asher Stern
 * Date: Nov 9, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfEmpiricalFeatureValueDistributionInCorpus<K,G>
{
	public CrfEmpiricalFeatureValueDistributionInCorpus(
			Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator,
					CrfFeaturesAndFilters<K, G> features)
	{
		super();
		this.corpusIterator = corpusIterator;
		this.features = features;
	}



	public void calculate()
	{
		empiricalFeatureValue = new double[features.getFilteredFeatures().length];
		for (int i=0;i<empiricalFeatureValue.length;++i) {empiricalFeatureValue[i]=0.0;}
		
		while (corpusIterator.hasNext())
		{
			List<? extends TaggedToken<K, G>> sentence = corpusIterator.next();
			K[] sentenceAsArray = CrfUtilities.extractSentence(sentence);
			int tokenIndex=0;
			G previousTag = null;
			for (TaggedToken<K, G> token : sentence)
			{
				Set<Integer> activeFeatureIndexes = CrfUtilities.getActiveFeatureIndexes(features,sentenceAsArray,tokenIndex,token.getTag(),previousTag);
				for (int index : activeFeatureIndexes)
				{
					CrfFilteredFeature<K, G> filteredFeature = features.getFilteredFeatures()[index];
					double featureValue = 0.0;
					if (filteredFeature.isWhenNotFilteredIsAlwaysOne())
					{
						featureValue = 1.0;
					}
					else
					{
						featureValue = filteredFeature.getFeature().value(sentenceAsArray,tokenIndex,token.getTag(),previousTag);
					}
					empiricalFeatureValue[index] = safeAdd(empiricalFeatureValue[index], featureValue);
				}
				
				++tokenIndex;
				previousTag = token.getTag();
			}
			if (tokenIndex!=sentence.size()) {throw new PosTaggerException("BUG");}
		}
	}
	
	

	public double[] getEmpiricalFeatureValue()
	{
		if (null==empiricalFeatureValue) {throw new PosTaggerException("Not calculated.");}
		return empiricalFeatureValue;
	}



	private final Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator;
	private final CrfFeaturesAndFilters<K, G> features;

	private double[] empiricalFeatureValue = null;
}
