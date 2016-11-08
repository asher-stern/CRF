package com.asher_stern.crf.utilities;

import java.math.BigDecimal;

/**
 * A collection of static functions for handling strings.
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class StringUtilities
{
	/**
	 * Provides a string representation for a given double array.
	 * @param array
	 * @return
	 */
	public static String arrayOfDoubleToString(double[] array)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean firstIteration = true;
		for (int i=0;i<array.length;++i)
		{
			if (firstIteration){firstIteration=false;} else{sb.append(",");}
			sb.append(String.format("%-3.3f", array[i]));
		}
		sb.append("]");
		
		return sb.toString();
	}

	
	public static String arrayOfBigDecimalToString(BigDecimal[] array)
	{
		StringBuilder sb = new StringBuilder();
		sb.append("[");
		boolean firstIteration = true;
		for (int i=0;i<array.length;++i)
		{
			if (firstIteration){firstIteration=false;} else{sb.append(",");}
			sb.append(bigDecimalToString(array[i]));
		}
		sb.append("]");
		
		return sb.toString();
	}
	
	public static String bigDecimalToString(BigDecimal d)
	{
		if (d.compareTo(ArithmeticUtilities.DOUBLE_MAX)<=0)
		{
			return String.format("%-3.4f", d.doubleValue());
		}
		else
		{
			return d.toString();
		}
	}

	
	/**
	 * Provides a string representation for the given array.
	 * @param array
	 * @return
	 */
	public static <T> String arrayToString(T[] array)
	{
		return arrayToString(array, "", "", " ");
	}
	
	/**
	 * Provides a string representation for the given array, where the prefix and suffix of the string,
	 * as well as the delimited between the array items are given as parameters.
	 * @param array
	 * @param prefix
	 * @param suffix
	 * @param delimiter
	 * @return
	 */
	public static <T> String arrayToString(T[] array, String prefix, String suffix, String delimiter)
	{
		StringBuilder sb = new StringBuilder();
		sb.append(prefix);
		boolean firstIteration = true;
		for (T t : array)
		{
			if (firstIteration) {firstIteration=false;}
			else {sb.append(delimiter);}
			
			sb.append(t);
		}
		sb.append(suffix);
		
		return sb.toString();
	}
	
	
	/**
	 * Checks whether the given string contains a letter.
	 * For example, for "43$!a00" the function would return true, while for "223344" the function would return false.
	 * @param str
	 * @return
	 */
	public static final boolean stringContainsLetter(String str)
	{
		char [] charArray = str.toCharArray();
		for (int index=0;index<charArray.length;++index)
		{
			if (Character.isLetter(charArray[index]))
			{
				return true;
			}
		}
		return false;
	}
	
	
}
