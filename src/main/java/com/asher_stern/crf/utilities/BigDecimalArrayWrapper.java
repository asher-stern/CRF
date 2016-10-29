package com.asher_stern.crf.utilities;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Wraps a {@link BigDecimal} array with equals() and hashCode()
 *
 * <p>
 * Date: Oct 29, 2016
 * @author Asher Stern
 *
 */
public class BigDecimalArrayWrapper
{
	public BigDecimalArrayWrapper(BigDecimal[] array)
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
		BigDecimalArrayWrapper other = (BigDecimalArrayWrapper) obj;
		if (!Arrays.equals(array, other.array))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return StringUtilities.arrayOfBigDecimalToString(array);
	}



	private BigDecimal[] array;
}
