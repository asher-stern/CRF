package com.asher_stern.crf.utilities;

import java.lang.reflect.Array;

/**
 * An array that starts not in index 0, but some other index.
 * <P>
 * A Java array starts in 0. This class provides an array which starts in another index.
 * So, for example, if the starting index is -3, and the length of the array is 5, than the
 * array indexes are [-3,-2,-1,0,1]. 
 * 
 * @author Asher Stern
 * Date: November 2014
 *
 * @param <T>
 */
public class ArbitraryRangeArray<T>
{
	@SuppressWarnings("unchecked")
	public ArbitraryRangeArray(int length, int firstIndex)
	{
		super();
		this.length = length;
		this.firstIndex = firstIndex;
		
		this.array = (T[]) Array.newInstance(Object.class, length); // new T[length];
	}

	public T get(int index)
	{
		return array[index-firstIndex]; 
	}
	
	public void set(int index, T value)
	{
		array[index-firstIndex] = value;
	}

	public int length()
	{
		return length;
	}
	
	public int getFirstIndex()
	{
		return firstIndex;
	}




	private final int length;
	private final int firstIndex;
	
	private T[] array;
}
