package org.postagging.utilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class StringUtilities
{
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
	
	public static <T> String arrayToString(T[] array)
	{
		return arrayToString(array, "", "", " ");
	}
	
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
	
	 
}
