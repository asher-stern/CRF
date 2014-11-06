package org.postagging.utilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class StringUtilities
{
	public static String arrayToString(double[] array)
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
}
