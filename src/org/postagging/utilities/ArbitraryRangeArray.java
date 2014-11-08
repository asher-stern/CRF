package org.postagging.utilities;

import java.lang.reflect.Array;

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
