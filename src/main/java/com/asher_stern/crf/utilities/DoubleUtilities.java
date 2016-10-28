package com.asher_stern.crf.utilities;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * 
 *
 * <p>
 * Date: Oct 15, 2016
 * @author Asher Stern
 *
 */
public class DoubleUtilities
{
	public static final MathContext MC = MathContext.DECIMAL128;
	
	public static final BigDecimal DOUBLE_MAX = big(Double.MAX_VALUE);
	
	public static final BigDecimal BIG_DECIMAL_E = big(Math.E);
	public static final BigDecimal BIG_DECIMAL_TWO = new BigDecimal("2.0", MC);
	
	public static BigDecimal big(double d)
	{
		return new BigDecimal(d, MC);
	}
	
	public static BigDecimal log(BigDecimal d)
	{
		if (d.compareTo(DOUBLE_MAX)<=0)
		{
			return big(Math.log(d.doubleValue()));
		}
		else
		{
			return safeAdd(BigDecimal.ONE, log(safeDivide(d, BIG_DECIMAL_E)));
		}
	}
	
	public static BigDecimal exp(BigDecimal d)
	{
		if (d.compareTo(DOUBLE_MAX)<=0)
		{
			return big(Math.exp(d.doubleValue()));
		}
		else
		{
			BigDecimal half = safeDivide(d, BIG_DECIMAL_TWO);
			BigDecimal halfExp = exp(half);
			return safeMultiply(halfExp, halfExp);
		}
	}
	
	public static BigDecimal safeAdd(final BigDecimal d1, final BigDecimal d2)
	{
		return d1.add(d2, MC);
	}
	
	public static BigDecimal safeSubtract(final BigDecimal d1, final BigDecimal d2)
	{
		return d1.subtract(d2, MC);
	}
	
	public static BigDecimal safeMultiply(final BigDecimal d1, final BigDecimal d2)
	{
		return d1.multiply(d2, MC);
	}
	
	public static BigDecimal safeMultiply(final BigDecimal d1, final BigDecimal d2, BigDecimal...ds)
	{
		BigDecimal ret = safeMultiply(d1, d2);
		for (BigDecimal d : ds)
		{
			ret = safeMultiply(ret, d);
		}
		return ret;
	}
	
	public static BigDecimal safeDivide(final BigDecimal d1, final BigDecimal d2)
	{
		return d1.divide(d2, MC);
	}
	
	
	
	
	public static double safeAdd(double d1, final double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		double ret = d1+d2;
		if (Double.isNaN(ret)) {throw new CrfException("Unexpected NaN double variable.");}
		ret = infinityToMaxDouble(ret);
		return ret;
	}
	
	public static double safeSubtract(double d1, double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		double ret = d1-d2;
		if (Double.isNaN(ret)) {throw new CrfException("Unexpected NaN double variable.");}
		ret = infinityToMaxDouble(ret);
		return ret;
	}
	
	public static double safeMultiply(double d1, double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		double sign = safeSign(d1)*safeSign(d2);
		double ret = infinityToMaxDouble(d1)*infinityToMaxDouble(d2);
		if (Double.isInfinite(ret))
		{
			ret = sign*Double.MAX_VALUE;
		}
		return ret;
	}
	
	public static double safeMultiply(double d1, double d2, double...ds)
	{
		double ret = safeMultiply(d1, d2);
		for (double d : ds)
		{
			ret = safeMultiply(ret, d);
		}
		return ret;
	}
	
	public static double safeDivide(double d1, double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		
		double sign = safeSign(d1)*safeSign(d2);
		double ret = infinityToMaxDouble(d1)/infinityToMaxDouble(d2);
		if (Double.isInfinite(ret))
		{
			ret = sign*Double.MAX_VALUE;
		}
		return ret;
	}
	
	
	public static double safeSign(double d)
	{
		if (Double.isNaN(d)) {throw new CrfException("Unexpected NaN double variable.");}
		double sign = Math.signum(d);
		if (sign == (-0.0)) {sign = 0.0;}
		return sign;
	}
	
	public static double infinityToMaxDouble(double d)
	{
		if (Double.POSITIVE_INFINITY==d)
		{
			return Double.MAX_VALUE;
		}
		else if (Double.NEGATIVE_INFINITY==d)
		{
			return -Double.MAX_VALUE;
		}
		else
		{
			return d;
		}
	}
}
