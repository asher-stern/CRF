package org.postagging.crf;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.utilities.TaggedToken;

import static org.postagging.crf.CrfUtilities.safeAdd;

/**
 * 
 * @author Asher Stern
 * Date: Nov 9, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfFeatureValueExpectationByModel<K, G>
{
	public CrfFeatureValueExpectationByModel(
			Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator,
			CrfModel<K, G> model)
	{
		super();
		this.corpusIterator = corpusIterator;
		this.model = model;
	}



	public void calculate()
	{
		featureValueExpectation = new double[model.getFeatures().getFilteredFeatures().length];
		for (int i=0;i<featureValueExpectation.length;++i) {featureValueExpectation[i]=0.0;}
		while (corpusIterator.hasNext())
		{
			List<? extends TaggedToken<K, G>> sentence = corpusIterator.next();
			addValueForSentence(sentence);
		}
	}
	
	
	public double[] getFeatureValueExpectation()
	{
		return featureValueExpectation;
	}



	private void addValueForSentence(List<? extends TaggedToken<K, G>> sentence)
	{
		K[] sentenceTokens = CrfUtilities.extractSentence(sentence);
		CrfForwardBackward<K,G> forwardBackward = new CrfForwardBackward<K,G>(model,sentenceTokens);
		forwardBackward.calculateForwardAndBackward();

		CrfFilteredFeature<K, G>[] filteredFeatures = model.getFeatures().getFilteredFeatures();
		for (int featureIndex=0;featureIndex<filteredFeatures.length;++featureIndex)
		{
			CrfFeature<K, G> feature = filteredFeatures[featureIndex].getFeature();
			
			double sum = 0.0;
			for (int sentenceIndex=0;sentenceIndex<sentenceTokens.length;++sentenceIndex)
			{
				Set<G> possiblePreviousTags = null;
				if (sentenceIndex==0) {possiblePreviousTags=Collections.singleton(null);}
				else {possiblePreviousTags=model.getTags();}
				for (G previousTag : possiblePreviousTags)
				{
					for (G currentTokenTag : model.getTags())
					{
						double featureValue = feature.value(sentenceTokens,sentenceIndex,currentTokenTag,previousTag);
						double alpha_forward_previousValue = 1.0;
						if (sentenceIndex>0)
						{
							alpha_forward_previousValue = forwardBackward.getAlpha_forward()[sentenceIndex-1].get(previousTag);
						}
						double beta_backward_value = forwardBackward.getBeta_backward().get(sentenceIndex).get(currentTokenTag);
						
						double probabilityUnderModel = alpha_forward_previousValue*featureValue*beta_backward_value;
						sum = safeAdd(sum, probabilityUnderModel);
					}
				}
			}
			featureValueExpectation[featureIndex] = safeAdd(featureValueExpectation[featureIndex], sum/forwardBackward.getCalculatedNormalizationFactor());
		}
	}
	
	
	

	private final Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator;
	private final CrfModel<K, G> model;
	
	private double[] featureValueExpectation;
}
