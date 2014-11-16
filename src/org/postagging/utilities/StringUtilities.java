package org.postagging.utilities;

import java.util.regex.Pattern;

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
	
	public static final boolean isLettersOnlyString(String str)
	{
		if (null==str) return true;
		return lettersOnlyPattern.matcher(str).matches();
	}
	
	
	private static final Pattern lettersOnlyPattern = Pattern.compile("[a-zA-Z]+");
	
	 
}
