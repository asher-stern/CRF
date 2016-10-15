package com.asher_stern.crf.crf;


import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.log4j.Logger;

import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.TaggedToken;
import static com.asher_stern.crf.utilities.DoubleUtilities.*;

/**
 * Calculates, for each feature, the expected sum of its values over the whole corpus.
 * For example, let's assume that the corpus contains 10 sentences, and each sentence contains 8 tokens,
 * so there are 80 tokens, and in each of them the feature is expected to get some value. In this class the sum of all those
 * expected values is calculated, for each feature.
 * 
 * @see CrfLogLikelihoodFunction
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
		for (int i=0;i<featureValueExpectation.length;++i) {featureValueExpectation[i]=0.0;} // Explicit initialization to zero, just to be on the safe side.
		
		ExecutorService executor = Executors.newWorkStealingPool();
		List<Future<?>> futures = new LinkedList<>();
		
		while (corpusIterator.hasNext())
		{
			final List<? extends TaggedToken<K, G>> sentence = corpusIterator.next();
			futures.add(executor.submit(
					new Runnable()
					{
						@Override
						public void run()
						{
							addValueForSentence(sentence);
						}
					}));
		}
		for (Future<?> future: futures)
		{
			try
			{
				future.get();
			}
			catch (InterruptedException | ExecutionException e)
			{
				throw new CrfException(e);
			}
		}
	}
	
	
	public double[] getFeatureValueExpectation()
	{
		return featureValueExpectation;
	}



	private void addValueForSentence(List<? extends TaggedToken<K, G>> sentence)
	{
		K[] sentenceTokens = CrfUtilities.extractSentence(sentence);
		
		// Find the "active" features for each triple of {token-index,tag-of-token,tag-of-previous-token}
		CrfRememberActiveFeatures<K, G> activeFeaturesForSentence = CrfRememberActiveFeatures.findForSentence(model.getFeatures(), model.getCrfTags(), sentenceTokens);
		
		// Calculate the CRF formula: e^{\Sum_{i=0}^{F-1}\theta_i*f_i(j,g,g')} where F is the number of features, j is a token index, g is a tag for that token, and g' is a tag for the previous token.
		CrfPsi_FormulaAllTokens<K, G> allTokensFormula = CrfPsi_FormulaAllTokens.createAndCalculate(model,sentenceTokens,activeFeaturesForSentence);
		
		CrfForwardBackward<K,G> forwardBackward = new CrfForwardBackward<K,G>(model,sentenceTokens,activeFeaturesForSentence);
		forwardBackward.setAllTokensFormulaValues(allTokensFormula);
		forwardBackward.calculateForwardAndBackward();

		final double normalizationFactor = forwardBackward.getCalculatedNormalizationFactor();
		
		for (int tokenIndex=0;tokenIndex<sentenceTokens.length;++tokenIndex)
		{
			for (G currentTag : model.getCrfTags().getTags())
			{
				Set<G> possiblePreviousTags = CrfUtilities.getPreviousTags(sentenceTokens, tokenIndex, currentTag, model.getCrfTags());
				for (G previousTag : possiblePreviousTags)
				{
					//Set<Integer> activeFeatures = CrfUtilities.getActiveFeatureIndexes(model.getFeatures(),sentenceTokens,tokenIndex,currentTag,previousTag);
					Set<Integer> activeFeatures = activeFeaturesForSentence.getOneTokenActiveFeatures(tokenIndex, currentTag, previousTag);
					for (int featureIndex : activeFeatures)
					{
						double featureValue = 0.0;
						if (model.getFeatures().getFilteredFeatures()[featureIndex].isWhenNotFilteredIsAlwaysOne())
						{
							featureValue = 1.0;
						}
						else
						{
							featureValue = model.getFeatures().getFilteredFeatures()[featureIndex].getFeature().value(sentenceTokens,tokenIndex,currentTag,previousTag);
						}

						Double probabilityUnderModel = null;

						if (featureValue!=0.0)
						{
							// Calculate probabilityUnderModel
							if (null==probabilityUnderModel)
							{
								double alpha_forward_previousValue = 1.0;
								if (tokenIndex>0)
								{
									alpha_forward_previousValue = forwardBackward.getAlpha_forward()[tokenIndex-1].get(previousTag);
								}
								double beta_backward_value = forwardBackward.getBeta_backward().get(tokenIndex).get(currentTag);
								//double psi_probabilityForGivenIndexAndTags = CrfUtilities.oneTokenFormula(model,sentenceTokens,tokenIndex,currentTag,previousTag,activeFeatures);
								double psi_probabilityForGivenIndexAndTags = allTokensFormula.getOneTokenFormula(tokenIndex,currentTag,previousTag);
								probabilityUnderModel = (alpha_forward_previousValue*psi_probabilityForGivenIndexAndTags*beta_backward_value)/normalizationFactor;
							}

							double addToExpectation = featureValue*probabilityUnderModel;

							synchronized(locker)
							{
								featureValueExpectation[featureIndex] = safeAdd(featureValueExpectation[featureIndex], addToExpectation);
							}
						}
					} // end for-each feature
				} // end for-each previous-tag
			} // end for-each current-tag
		} // end for-each token-index
		
	}
	
	
	

	private final Iterator<? extends List<? extends TaggedToken<K, G>>> corpusIterator;
	private final CrfModel<K, G> model;
	
	private final Object locker = new Object();
	private double[] featureValueExpectation;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CrfFeatureValueExpectationByModel.class);
}
