package org.postagging.utilities;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;


/**
 * A data structure that stores the top-k items of the inserted items.
 * It is assumed that the items are comparable, so "top-k" is well defined.
 * <BR>
 * Time complexity of each insertion is amortized O(log(k)).
 * Space complexity is O(k^2)
 *  
 * @author Asher Stern
 * Date: Nov 19, 2014
 *
 * @param <T>
 */
public class TopK_DateStructure<T>
{
	public TopK_DateStructure(int k)
	{
		this(k, null);
	}
	
	
	@SuppressWarnings("unchecked")
	public TopK_DateStructure(int k, Comparator<T> comparator)
	{
		super();
		this.k = k;
		this.comparator = comparator;
		
		this.array_length = k*k + k;
		this.storage = (T[]) Array.newInstance(Object.class, array_length);
		
		if (k<=0) {throw new PosTaggerException("k <= 0");}
		
		index = 0;
	}



	/**
	 * Insert an item to this data-structure. This item might be discarded later when it is absolutely sure that it is not
	 * one of the top-k items that were inserted to this data-structure.
	 * @param item
	 */
	public void insert(T item)
	{
		storage[index] = item;
		++index;
		
		if (index>array_length) {throw new PosTaggerException("BUG");}
		if (index==array_length)
		{
			sortAndShrink();
		}
	}
	
	/**
	 * Get the top-k items among all the items that were inserted to this data-structure so far.
	 * <B>Note that this method has time complexity of O(k*log(k)).</B>
	 * @return
	 */
	public ArrayList<T> getTopK()
	{
		if (index>k)
		{
			sortAndShrink();
		}
		
		ArrayList<T> topKAsList = new ArrayList<T>();
		for (int i=0;i<k;++i)
		{
			topKAsList.add(storage[i]);
		}
		return topKAsList;
	}
	
	
	private void sortAndShrink()
	{
		if (null==comparator)
		{
			Arrays.sort(storage, 0, index, Collections.reverseOrder());
		}
		else
		{
			Arrays.sort(storage, 0, index, Collections.reverseOrder(comparator));
		}
		
		index = k;
	}

	private final int k;
	private final int array_length;
	private final T[] storage;
	
	private int index = 0; // the index of the next item to insert
	private Comparator<T> comparator = null;
}
