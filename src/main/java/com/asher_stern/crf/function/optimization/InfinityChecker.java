package com.asher_stern.crf.function.optimization;

/**
 * Utility class to check whether some value is infinity, and throw exception if it is.
 * <br>
 * Use the static methods <code>check()</code>.
 * Chained call can be employed as well: <code> check(value1).check(value2);  </code>. The values can be either double or double[].
 *
 * <p>
 * Date: Oct 10, 2016
 * @author Asher Stern
 *
 */
public final class InfinityChecker
{
	/**
	 * Throws {@link InfinityException} if one of the given values is either infinity or NaN
	 */
	public static final NestedInfinityChecker check(double... values)
	{
		return NestedInfinityChecker.INSTANCE.check(values);
	}
	
	/**
	 * Throws {@link InfinityException} if one of the elements in one of the given vectors is either infinity or NaN
	 */
	public static final NestedInfinityChecker check(double[]... values)
	{
		return NestedInfinityChecker.INSTANCE.check(values);
	}

	/**
	 * Throws {@link InfinityException} if the given value is either infinity or NaN.
	 * @return the given value.
	 */
	public static final double checked(double value)
	{
		NestedInfinityChecker.checkValue(value);
		return value;
	}
	
	/**
	 * Throws {@link InfinityException} if one of the given values in the given vector is either infinity or NaN.
	 * @return the given vector.
	 */
	public static final double[] checked(double[] vector)
	{
		NestedInfinityChecker.checkVector(vector);
		return vector;
	}


	
	
	
	
	/////////////// IMPLEMENTATION ///////////////
	
	public static class NestedInfinityChecker
	{
		public final NestedInfinityChecker check(double... values)
		{
			for (double value : values)
			{
				checkValue(value);
			}
			return INSTANCE;
		}
		
		public final NestedInfinityChecker check(double[]... values)
		{
			for (double[] value : values)
			{
				checkVector(value);
			}
			return INSTANCE;
		}
		
		private static final void checkValue(double value)
		{
			if (Double.isInfinite(value))
			{
				throw new InfinityException(true, false, false);
			}
			if (Double.isNaN(value))
			{
				throw new InfinityException(true, false, true);
			}
		}
		
		private static final void checkVector(double[] vector)
		{
			for (double d : vector)
			{
				if (Double.isInfinite(d))
				{
					throw new InfinityException(false, true, false);
				}
				if (Double.isNaN(d))
				{
					throw new InfinityException(false, true, true);
				}
			}
		}
		
		protected NestedInfinityChecker(){}
		private static final NestedInfinityChecker INSTANCE = new NestedInfinityChecker();
	}
}
