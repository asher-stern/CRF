package org.postagging.crf;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.postagging.function.DerivableFunction;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

/**
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
					
					double debug_old = sumWeightedFeatures;
					sumWeightedFeatures += weightedFeature;
					if ( ( (debug_old>sumWeightedFeatures)&&(weightedFeature>0.0) ) || ( (debug_old<sumWeightedFeatures)&&(weightedFeature<0.0) ) ) {throw new PosTaggerException("Error: seems like a limitation of the \"double\" type.");}
				}
				if (parameterIterator.hasNext()||featureIterator.hasNext()) {throw new PosTaggerException("BUG");}
				
				previousTag = taggedToken.getTag();
				++tokenIndex;
			}
			if (tokenIndex!=sentence.size()) {throw new PosTaggerException("BUG");}
		}
		
		return sumWeightedFeatures;
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
			double debug_old = ret;
			ret += component*component;
			if (debug_old>ret) {throw new PosTaggerException("Cannot calculate norm square due to a limitation of the \"double\" type.");}
		}
		return ret;
	}

	private final Iterable<List<? extends TaggedToken<K, G> >> corpus;
	private final Set<G> tags;
	private final ArrayList<CrfFeature<K, G>> features;
	private final double sigmaSquare_inverseRegularizationFactor;
}
