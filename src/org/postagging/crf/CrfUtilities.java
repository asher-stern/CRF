package org.postagging.crf;

import java.lang.reflect.Array;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.crf.features.Filter;
import org.postagging.utilities.TaggedToken;
import org.postagging.utilities.PosTaggerException;

/**
 * A collection of static functions needed by CRF.
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 */
public class CrfUtilities
{
	/**
	 * Adds all the items in "fromCollection" into "intoCollection".
	 * <BR>
	 * There was a bug in some implementations of Collection.addAll() method in some versions of J2SE, so to be on the
	 * safe side, I implement it here.
	 * @param intoCollection
	 * @param fromCollection
	 */
	public static <T> void addAll(Collection<T> intoCollection, Collection<? extends T> fromCollection)
	{
		for (T t : fromCollection)
		{
			intoCollection.add(t);
		}
	}
	
	/**
	 * Finds and returns the feature-indexes for which it is not sure that they return 0.<BR>
	 * Typically in CRF, most of the features return 0 in most inputs.
	 * For example, a feature that returns 1 if the token is "the" and its tag is "DETERMINER". This feature returns 0 for most
	 * of the words in the corpus, and most of the tags.<BR>
	 * This static function returns the features, for a given token and tags, that <b>might</b> return non-zero.
	 * <BR>
	 * See also {@link Filter}
	 * 
	 * 
	 * @param features
	 * @param sentence
	 * @param tokenIndex
	 * @param currentTag
	 * @param previousTag
	 * @return
	 */
	public static <K,G> Set<Integer> getActiveFeatureIndexes(CrfFeaturesAndFilters<K,G> features, K[] sentence, int tokenIndex, G currentTag, G previousTag)
	{
		Set<Integer> activeFeatureIndexes = new LinkedHashSet<Integer>();
		addAll(activeFeatureIndexes, features.getIndexesOfFeaturesWithNoFilter());
		
		K token = sentence[tokenIndex];
		Set<Filter<K, G>> filters = features.getFilterFactory().createFilters(token, currentTag, previousTag);
		for (Filter<K, G> filter : filters)
		{
			Set<Integer> featureIndexesForFilter = features.getMapActiveFeatures().get(filter);
			if (featureIndexesForFilter!=null)
			{
				addAll(activeFeatureIndexes, featureIndexesForFilter);
			}
		}
		
		return activeFeatureIndexes;
	}
	
	/**
	 * Returns \Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}, where k is the number of features, \theta_i is parameter number i,
	 * f_i is feature number i, x is the given sentence, j is the index of the token, s is the tag of token number j,
	 * and s' is the tag of token number j-1.
	 * 
	 * @param model the CRF model: holds the features and the parameters.
	 * @param sentence a sentence (sequence of tokens)
	 * @param tokenIndex token index
	 * @param currentTag tag of the token in tokenIndex
	 * @param previousTag tag of the token in tokenIndex-1
	 * @return \Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}
	 */
	public static <K,G> double oneTokenSumWeightedFeatures(CrfModel<K, G> model, K[] sentence, int tokenIndex, G currentTag, G previousTag)
	{
		Set<Integer> activeFeatureIndexes = getActiveFeatureIndexes(model.getFeatures(),sentence,tokenIndex,currentTag,previousTag);
		return oneTokenSumWeightedFeatures(model,sentence,tokenIndex,currentTag,previousTag,activeFeatureIndexes);
	}

	/**
	 * Returns \Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}, where k is the number of features, \theta_i is parameter number i,
	 * f_i is feature number i, x is the given sentence, j is the index of the token, s is the tag of token number j,
	 * and s' is the tag of token number j-1.
	 * <BR>
	 * This function is also given a set of features for which <b>it is not known</b> that they return zero for (x,j,s,s').
	 * See {@link #getActiveFeatureIndexes(CrfFeaturesAndFilters, Object[], int, Object, Object)}.
	 *  
	 * @param model the CRF model: holds the features and the parameters.
	 * @param sentence a sentence (sequence of tokens)
	 * @param tokenIndex token index
	 * @param currentTag tag of the token in tokenIndex
	 * @param previousTag tag of the token in tokenIndex-1
	 * @param knownActiveFeatureIndexes a set of features for which <b>it is not known</b> that they return zero for (x,j,s,s').
	 * @return \Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}
	 */
	public static <K,G> double oneTokenSumWeightedFeatures(CrfModel<K, G> model, K[] sentence, int tokenIndex, G currentTag, G previousTag, Set<Integer> knownActiveFeatureIndexes)
	{
		double sum = 0.0;
		for (int index : knownActiveFeatureIndexes)
		{
			CrfFilteredFeature<K, G> feature = model.getFeatures().getFilteredFeatures()[index];
			double featureValue = 0.0;
			if (feature.isWhenNotFilteredIsAlwaysOne())
			{
				featureValue = 1.0;
			}
			else
			{
				featureValue = feature.getFeature().value(sentence,tokenIndex,currentTag,previousTag);
			}
			
			double weightedValue = model.getParameters().get(index)*featureValue;
			sum = safeAdd(sum, weightedValue);
		}
		return sum;
	}
	
	/**
	 * Returns e^{\Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}}, where k is the number of features, \theta_i is parameter number i,
	 * f_i is feature number i, x is the given sentence, j is the index of the token, s is the tag of token number j,
	 * and s' is the tag of token number j-1.
	 * 
	 * @param model the CRF model: holds the features and the parameters.
	 * @param sentence a sentence (sequence of tokens)
	 * @param tokenIndex token index
	 * @param currentTag tag of the token in tokenIndex
	 * @param previousTag tag of the token in tokenIndex-1
	 * @return e^{\Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}}
	 */
	public static <K,G> double oneTokenFormula(CrfModel<K, G> model, K[] sentence, int tokenIndex, G currentTag, G previousTag)
	{
		Set<Integer> activeFeatureIndexes = getActiveFeatureIndexes(model.getFeatures(),sentence,tokenIndex,currentTag,previousTag);
		return oneTokenFormula(model,sentence,tokenIndex,currentTag,previousTag,activeFeatureIndexes);
	}
	
	/**
	 * Returns e^{\Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}}, where k is the number of features, \theta_i is parameter number i,
	 * f_i is feature number i, x is the given sentence, j is the index of the token, s is the tag of token number j,
	 * and s' is the tag of token number j-1.
	 * <BR>
	 * This function is also given a set of features for which <b>it is not known</b> that they return zero for (x,j,s,s').
	 * See {@link #getActiveFeatureIndexes(CrfFeaturesAndFilters, Object[], int, Object, Object)}.
	 * 
	 * @param model the CRF model: holds the features and the parameters.
	 * @param sentence a sentence (sequence of tokens)
	 * @param tokenIndex token index
	 * @param currentTag tag of the token in tokenIndex
	 * @param previousTag tag of the token in tokenIndex-1
	 * @param knownActiveFeatureIndexes a set of features for which <b>it is not known</b> that they return zero for (x,j,s,s').
	 * @return e^{\Sum_{i=0}^{k-1}{\theta_i*f_i(x,j,s,s')}}
	 */
	public static <K,G> double oneTokenFormula(CrfModel<K, G> model, K[] sentence, int tokenIndex, G currentTag, G previousTag,Set<Integer> knownActiveFeatureIndexes)
	{
		return Math.exp(oneTokenSumWeightedFeatures(model,sentence,tokenIndex,currentTag,previousTag,knownActiveFeatureIndexes));
	}
	
	
	/**
	 * Returns a sentence as an array, for the given sentence (given as list of tagged tokens)
	 * @param sentence a sentence
	 * @return the given sentence as an array.
	 */
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
	
	/**
	 * Return variable+valueToAdd.
	 * The user, instead of writing variable += valueToAdd, writes variable = safeAdd(variable,valueToAdd).
	 * This function is required in order to be on the safe side,
	 * in detecting whether a limitation of the "double" type caused unexpected results.
	 *  
	 * @param variable
	 * @param valueToAdd
	 * @return variable+valueToAdd.
	 */
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
	
	/**
	 * If |value1|>|value2| returns |value1|/|value2|. Otherwise returns |value2|/|value1|.
	 */
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
	
	/**
	 * Given a map from K to set of V - adds v to the set of k.
	 * @param map
	 * @param key
	 * @param value
	 */
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

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CrfUtilities.class);
}
