package org.postagging.crf;

import java.util.Iterator;

import org.postagging.utilities.PosTaggerException;

public class CrfFormula
{
	public static <K,G> double oneTokenFormula(CrfModel<K, G> model, K[] sentence, int tokenIndex, G currentTag, G previousTag)
	{
		boolean debug_activeFeatureDetected = false;
		double sum = 0.0;
		Iterator<CrfFeature<K, G>> featureIterator = model.getFeatures().iterator();
		Iterator<Double> parameterIterator = model.getParameters().iterator();
		while (featureIterator.hasNext()&&parameterIterator.hasNext())
		{
			CrfFeature<K, G> feature = featureIterator.next();
			double parameter = parameterIterator.next();
			
			double featureValue = feature.value(sentence, tokenIndex, currentTag, previousTag);
			if (featureValue!=0.0){debug_activeFeatureDetected=true;}
			sum += parameter*featureValue;
		}
		if (featureIterator.hasNext()||parameterIterator.hasNext()) {throw new PosTaggerException("Number of parameters differs from number of features.");}
		
		if (!debug_activeFeatureDetected) {throw new PosTaggerException("Bug: no active feature detected for the given token. Token-index = "+tokenIndex);}
		return Math.exp(sum);
	}

}
