package com.asher_stern.crf.utilities;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;


/**
 * A collection of static helper functions for pos-tagger. 
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class MiscellaneousUtilities
{
	public static final long RANDOM_SELECTION_RANDOM_SEED = 101112L; // Just a number. Make it deterministic.
	
	
	/**
	 * Finds all the tags that exist in the given corpus.
	 * @param corpus A corpus of POS-tagged sentences.
	 * @return All the tags in the given corpus.
	 */
	public static Set<String> extractAllTagsFromCorpus(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus)
	{
		Set<String> allTags = new LinkedHashSet<String>();
		for (List<? extends TaggedToken<String, String> > sentence : corpus)
		{
			for (TaggedToken<String, String> taggedToken : sentence)
			{
				allTags.add(taggedToken.getTag());
			}
		}
		return allTags;
	}
	
	/**
	 * Tests whether the given two objects are equal. Null objects are permitted here.
	 * @param t1
	 * @param t2
	 * @return
	 */
	public static <T> boolean equalObjects(T t1, T t2)
	{
		if (t1==t2) return true;
		if ( (t1==null) || (t2==null) ) return false;
		return t1.equals(t2);
	}
	
	
	/**
	 * Selects equally-distributed numbers from the given range.
	 * 
	 * @param howMany
	 * @param size specifies that the range of selection is 0..size (not including <code>size</code>)
	 * @return
	 */
	public static LinkedHashSet<Integer> selectRandomlyFromRange(int howMany, int size)
	{
		Random random = new Random(RANDOM_SELECTION_RANDOM_SEED);
		Map<Integer, Integer> replacements = new LinkedHashMap<>();
		LinkedHashSet<Integer> ret = new LinkedHashSet<>();
		for (int counter = 0; counter<howMany; ++counter)
		{
			Integer selected = random.nextInt(size-counter);
			final Integer originalSelected = selected;
			while (replacements.containsKey(selected))
			{
				selected = replacements.get(selected);
			}
			replacements.put(originalSelected, size-counter-1);
			ret.add(selected);
		}
		return ret;
	}
	
	public static <E> List<E> selectRandomlyFromList(int howMany, List<E> list)
	{
		if (list.size()<howMany) throw new CrfException("Illegal argument: "+howMany+">"+list.size());
		Set<Integer> selectedIndexes = selectRandomlyFromRange(howMany, list.size());
		List<E> ret = new ArrayList<>(howMany);
		int index=0;
		for (E element : list)
		{
			if (selectedIndexes.contains(index))
			{
				ret.add(element);
			}
			++index;
		}
		return ret;
	}
	
	
	/**
	 * Creates and returns a new map from Integer to V, where the keys are the position of the items in the list.<BR>
	 * For example, for a List<String> {"abc", "def", "ghi"}, the returned map would be:
	 * {1 --> "abc"}, {2 --> "def"}, {3 --> "ghi"}
	 * @param list A list
	 * @return A map whose values are the list items, and the keys are their positions (indexes) in the list.
	 */
	public static <V> Map<Integer, V> listToMap(List<V> list)
	{
		Map<Integer, V> map = new LinkedHashMap<Integer, V>();
		int index=0;
		for (V v : list)
		{
			map.put(index, v);
			++index;
		}
		return map;
	}
	
	/**
	 * Creates and returns a list of the map's keys, sorted by their values, according to their natural ordering.
	 */
	public static <K,V  extends Comparable<V>> List<K> sortByValue(Map<K, V> map)
	{
		return sortByValue(map, new NaturalComparator<V>());
	}

	/**
	 * Creates and returns a list of the map's keys, sorted by their values, according to the given comparator.
	 * 
	 * @param map A map.
	 * @param valueComparator Defines ordering relation of the map's values
	 * @return List of the map's keys, sorted by their values.
	 */
	public static <K,V> List<K> sortByValue(Map<K, V> map, Comparator<V> valueComparator)
	{
		List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K,V>>(map.entrySet().size());
		for (Map.Entry<K, V> entry : map.entrySet())
		{
			list.add(entry);
		}
		
		Comparator<Map.Entry<K, V>> comparator = new Comparator<Map.Entry<K, V>>()
		{
			@Override
			public int compare(Entry<K, V> o1, Entry<K, V> o2)
			{
				if (o1.getValue()==o2.getValue()) return 0;
				if (o1.getValue()==null) return -1;
				if (o2.getValue()==null) return 1;
				return valueComparator.compare(o1.getValue(), o2.getValue());
			}
		};
		
		list.sort(comparator);
		
		List<K> ret = new ArrayList<K>(list.size());
		for (Map.Entry<K, V> entry : list)
		{
			ret.add(entry.getKey());
		}
		return ret;
	}
	
	
	/**
	 * A comparator which compares to objects according to their natural ordering.
	 *
	 * @param <T>
	 */
	private static class NaturalComparator<T extends Comparable<T>> implements Comparator<T>
	{
		@Override
		public int compare(T o1, T o2)
		{
			return o1.compareTo(o2);
		}
	}
	
}
