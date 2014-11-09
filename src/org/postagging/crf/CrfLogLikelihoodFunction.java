package org.postagging.crf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.postagging.function.DerivableFunction;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

import static org.postagging.crf.CrfUtilities.safeAdd;

/**
 * This function is CONCAVE, not convex!!!
 * 
 * @author Asher Stern
 * Date: Nov 9, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfLogLikelihoodFunction<K,G> extends DerivableFunction
{
	@Override
	public double value(double[] point)
	{
		CrfModel<K, G> model = createModel(point);
		
		return calculateSumWeightedFeatures(model) - calculateSumOfLogNormalizations(model) - calculateRegularizationFactor(point);
	}
	

	@Override
	public double[] gradient(double[] point)
	{
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int size()
	{
		return features.size();
	}

	
	
	
	private double calculateSumWeightedFeatures(CrfModel<K, G> model)
	{
		double sumWeightedFeatures = 0.0;
		for (List<? extends TaggedToken<K, G> > sentence : corpus)
		{
			K[] sentenceAsArray = CrfUtilities.extractSentence(sentence);
			G previousTag = null;
			int tokenIndex=0;
			for (TaggedToken<K, G> taggedToken : sentence)
			{
				Iterator<Double> parameterIterator = model.getParameters().iterator();
				Iterator<CrfFeature<K, G>> featureIterator = model.getFeatures().iterator();
				while (parameterIterator.hasNext()&&featureIterator.hasNext())
				{
					double parameter = parameterIterator.next();
					CrfFeature<K, G> feature = featureIterator.next();
					double featureValue = feature.value(sentenceAsArray,tokenIndex,taggedToken.getTag(),previousTag);
					double weightedFeature = parameter*featureValue;
					
					sumWeightedFeatures = safeAdd(sumWeightedFeatures, weightedFeature);
				}
				if (parameterIterator.hasNext()||featureIterator.hasNext()) {throw new PosTaggerException("BUG");}
				
				previousTag = taggedToken.getTag();
				++tokenIndex;
			}
			if (tokenIndex!=sentence.size()) {throw new PosTaggerException("BUG");}
		}
		
		return sumWeightedFeatures;
	}
	
	private double calculateSumOfLogNormalizations(CrfModel<K, G> model)
	{
		double sum = 0.0;
		for (List<? extends TaggedToken<K, G> > sentence : corpus)
		{
			K[] sentenceAsArray = CrfUtilities.extractSentence(sentence);
			CrfForwardBackward<K, G> forwardBackward = new CrfForwardBackward<K, G>(model,sentenceAsArray);
			forwardBackward.calculateForwardAndBackward();
			
			double normalizationFactor = forwardBackward.getCalculatedNormalizationFactor();
			double logNormalizationFactor = Math.log(normalizationFactor);
			sum = safeAdd(sum, logNormalizationFactor);
		}
		return sum;
	}
	
	private double calculateRegularizationFactor(double[] parameters)
	{
		return normSquare(parameters)/(2*sigmaSquare_inverseRegularizationFactor);
	}

	
	private CrfModel<K, G> createModel(double[] point)
	{
		if (point.length!=features.size()) {throw new PosTaggerException("Number of parameters differs from number of features.");}
		ArrayList<Double> parameters = new ArrayList<Double>(point.length);
		for (double parameter : point)
		{
			parameters.add(parameter);
		}
		return new CrfModel<K, G>(tags,features,parameters);
	}
	
	private double normSquare(double[] vector)
	{
		double ret = 0.0;
		for (double component : vector)
		{
			ret = safeAdd(ret, component*component);
		}
		return ret;
	}

	private final Iterable<List<? extends TaggedToken<K, G> >> corpus;
	private final Set<G> tags;
	private final ArrayList<CrfFeature<K, G>> features;
	private final double sigmaSquare_inverseRegularizationFactor;
}
