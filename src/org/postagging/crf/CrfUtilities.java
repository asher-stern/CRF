package org.postagging.crf;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.postagging.utilities.StringUtilities;
import org.postagging.utilities.TaggedToken;
import org.postagging.utilities.PosTaggerException;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 */
public class CrfUtilities
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
		
		if (!debug_activeFeatureDetected) {throw new PosTaggerException("Bug: no active feature detected for the given token.\n"
				+ "Token = "+sentence[tokenIndex]+". Current tag = "+currentTag+". Previous tag = "+previousTag+". Token-index = "+tokenIndex
				+"\nSentence = "+StringUtilities.arrayToString(sentence));}
		return Math.exp(sum);
	}
	
	public static <K> K[] extractSentence(List<? extends TaggedToken<K, ?>> sentence)
	{
		if (sentence==null) throw new PosTaggerException("The input is an empty sentence.");
		if (sentence.size()<1) throw new PosTaggerException("The input is an empty sentence.");
		@SuppressWarnings("unchecked")
		K[] ret = (K[]) Array.newInstance(sentence.iterator().next().getToken().getClass(), sentence.size());
		int index=0;
		for (TaggedToken<K, ?> taggedToken : sentence)
		{
			ret[index] = taggedToken.getToken();
			++index;
		}
		if (index!=ret.length) {throw new PosTaggerException("BUG");}
		return ret;
	}
	
	
	public static double safeAdd(double variable, final double valueToAdd)
	{
		final double oldValue = variable;
		variable += valueToAdd;
		if ( ( (valueToAdd<0.0) && (oldValue<variable) ) || ( (valueToAdd>0.0) && (oldValue>variable) ) )
		{
			throw new PosTaggerException("Error: adding value to \"double\" variable yielded unexpected results. This seems like a limitation of double."
					+ "variable was: "+String.format("%-3.3", oldValue)+", value to add was: "+String.format("%-3.3f", valueToAdd));
		}
		return variable;
	}
	
	public static double relativeDifference(double value1, double value2)
	{
		double smaller;
		double larger;
		if (value1<value2)
		{
			smaller = Math.abs(value1);
			larger = Math.abs(value2);
		}
		else
		{
			smaller = Math.abs(value2);
			larger = Math.abs(value1);
		}
		return larger/smaller;
	}
	
	public static <K,V> void putInMapSet(Map<K, Set<V>> map, K key, V value)
	{
		Set<V> set = map.get(key);
		if (null==set)
		{
			set = new LinkedHashSet<V>();
			map.put(key, set);
		}
		set.add(value);
	}

}
