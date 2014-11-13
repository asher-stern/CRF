package org.postagging.utilities;

import java.util.Arrays;

/**
 * Wraps double[] with {@link #equals(Object)} and {@link #hashCode()}.
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 */
public class DoubleArrayWrapper
{
	public DoubleArrayWrapper(double[] array)
	{
		super();
		this.array = array;
	}

	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(array);
		return result;
	}



	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DoubleArrayWrapper other = (DoubleArrayWrapper) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		return true;
	}
	

	@Override
	public String toString()
	{
		return StringUtilities.arrayOfDoubleToString(array); 
	}

	private final double[] array;
}
