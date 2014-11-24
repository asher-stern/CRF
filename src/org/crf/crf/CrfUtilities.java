package org.crf.crf;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.crf.crf.filters.CrfFeaturesAndFilters;
import org.crf.crf.filters.CrfFilteredFeature;
import org.crf.crf.filters.Filter;
import org.crf.utilities.CrfException;
import org.crf.utilities.TaggedToken;

/**
 * A collection of static functions needed by CRF.
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 */
public class CrfUtilities
{
	public static final double ROUGHLY_EQUAL_DISTANCE_FROM_ZERO = 0.001;
	public static final double ROUGHLY_EQUAL_DEVIATION_FROM_ONE = 0.01;

	
	/**
	 * Returns the set of tags that can be assigned to the token which precedes the given token, assuming "currentTag" is
	 * the tag of the current token.
	 * @param sentence a sequence of tokens
	 * @param index the index of the "current token"
	 * @param currentTag the tag of the "current token"
	 * @param crfTags A data-structure that holds all the tags in the training corpus, and the restrictions over them.
	 * @return the set of tags that can be assigned to the token which precedes the given token, assuming "currentTag" is
	 * the tag of the current token.
	 */
	public static <K,G> Set<G> getPreviousTags(K[] sentence, int index, G currentTag, CrfTags<G> crfTags)
	{
		Set<G> previousTags = null;
		if (index<0) throw new CrfException("Error: no tag can precede the virtual token that precedes the first token.");
		if (index==0)
		{
			previousTags = crfTags.getPrecedeWhenFirst().get(currentTag);
		}
		else
		{
			previousTags = crfTags.getCanPrecedeNonNull().get(currentTag);
		}
		return previousTags;
	}
	
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
		
		Set<Filter<K, G>> filters = features.getFilterFactory().createFilters(sentence, tokenIndex, currentTag, previousTag);
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
		if (sentence==null) throw new CrfException("The input is an empty sentence.");
		if (sentence.size()<1) throw new CrfException("The input is an empty sentence.");
		@SuppressWarnings("unchecked")
		K[] ret = (K[]) Array.newInstance(sentence.iterator().next().getToken().getClass(), sentence.size());
		int index=0;
		for (TaggedToken<K, ?> taggedToken : sentence)
		{
			ret[index] = taggedToken.getToken();
			++index;
		}
		if (index!=ret.length) {throw new CrfException("BUG");}
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
			throw new CrfException("Error: adding value to \"double\" variable yielded unexpected results. This seems like a limitation of double."
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
	 * Returns true if the two given numbers are roughly equal.
	 * <BR>
	 * The numbers are considered "roughly equal" if dividing the (absolute value of the) larger by the (absolute value of the)
	 * smaller is around 1, where "around 1" means no larger than 1.01.
	 * <BR>
	 * In case they are near 0, then the criterion is that subtracting one from the other yields absolute value no larger then 0.001.
	 *  
	 * @param value1
	 * @param value2
	 * @return
	 */
	public static boolean roughlyEqual(double value1, double value2)
	{
		boolean ret = true;
		if ( ( (value1<0.0) || (value2<0.0) ) &&  ( (value1>=0.0) || (value2>=0.0) ) )
		{
			double gap = Math.abs(value1-value2);
			if (gap>ROUGHLY_EQUAL_DISTANCE_FROM_ZERO)
			{
				ret = false;
			}
		}
		else
		{
			if ((relativeDifference(value1,value2)-1.0)>ROUGHLY_EQUAL_DEVIATION_FROM_ONE)
			{
				ret = false;
			}
		}
		return ret;
		
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
