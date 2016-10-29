package com.asher_stern.crf.utilities;

import java.math.BigDecimal;
import java.util.Comparator;


/**
 * 
 *
 * <p>
 * Date: Oct 29, 2016
 * @author Asher Stern
 *
 */
public class AbsoluteBigDecimalValueComparator implements Comparator<BigDecimal>
{

	@Override
	public int compare(BigDecimal o1, BigDecimal o2)
	{
		if (o1==o2) return 0;
		if (o1==null) return -1;
		if (o2==null) return 1;
		return o1.abs().compareTo(o2.abs());
	}

}
