package com.asher_stern.crf.crf;

import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.asher_stern.crf.crf.filters.CrfFeaturesAndFilters;
import com.asher_stern.crf.crf.filters.CrfFilteredFeature;
import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.TaggedToken;
import static com.asher_stern.crf.utilities.ArithmeticUtilities.*;

/**
 * Calculates the sum of all feature-values over the whole corpus.
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
		empiricalFeatureValue = new BigDecimal[features.getFilteredFeatures().length];
		for (int i=0;i<empiricalFeatureValue.length;++i) {empiricalFeatureValue[i]=BigDecimal.ZERO;}
		
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
					empiricalFeatureValue[index] = safeAdd(empiricalFeatureValue[index], big(featureValue));
				}
				
				++tokenIndex;
				previousTag = token.getTag();
			}
			if (tokenIndex!=sentence.size()) {throw new CrfException("BUG");}
		}
	}
	
	

	public BigDecimal[] getEmpiricalFeatureValue()
	{
		if (null==empiricalFeatureValue) {throw new CrfException("Not calculated.");}
		return empiricalFeatureValue;
	}



	private final Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator;
	private final CrfFeaturesAndFilters<K, G> features;

	private BigDecimal[] empiricalFeatureValue = null;
}
