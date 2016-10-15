package com.asher_stern.crf.utilities;

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
	public static double safeAdd(double d1, final double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		double ret = d1+d2;
		if (Double.isNaN(ret)) {throw new CrfException("Unexpected NaN double variable.");}
		ret = infinityToMaxDouble(ret);
		return ret;
	}
	
	public static double safeSubtract(double d1, final double d2)
	{
		d1 = -d1;
		return safeAdd(d1, d2);
	}
	
	public static double safeMultiply(double d1, double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		double sign = safeSign(d1)*safeSign(d2);
		double ret = d1*d2;
		if (Double.isInfinite(ret))
		{
			ret = sign*Double.MAX_VALUE;
		}
		return ret;
	}
	
	public static double safeDivide(double d1, double d2)
	{
		if (Double.isNaN(d1)) {throw new CrfException("Unexpected NaN double variable.");}
		if (Double.isNaN(d2)) {throw new CrfException("Unexpected NaN double variable.");}
		
		double sign = safeSign(d1)*safeSign(d2);
		double ret = d1/d2;
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
