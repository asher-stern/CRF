package org.crf.utilities;

import java.util.Comparator;

/**
 * A comparator of Double, which compares the absolute value of the given Doubles.
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public class AbsoluteValueComparator implements Comparator<Double>
{
	@Override
	public int compare(Double o1, Double o2)
	{
		if (o1==o2) return 0;
		if (o1==null) return -1;
		if (o2==null) return 1;
		return Double.compare(Math.abs(o1), Math.abs(o2));
	}
	
}