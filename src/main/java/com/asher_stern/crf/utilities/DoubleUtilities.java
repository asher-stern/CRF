package com.asher_stern.crf.utilities;

import java.math.BigDecimal;
import java.math.MathContext;

import org.apache.log4j.Logger;

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
	public static final BigDecimal BIG_DECIMAL_E_TO_512 = BIG_DECIMAL_E.pow(512);
	public static final BigDecimal BIG_DECIMAL_512 = new BigDecimal("512", MC);
	
	public static BigDecimal big(double d)
	{
		return new BigDecimal(d, MC);
	}
	
//	public static BigDecimal log(BigDecimal d)
//	{
//		return log(d, true);
//	}
	

	public static BigDecimal log(BigDecimal d)
	{
		long debug_counter = 0;
		BigDecimal ret = BigDecimal.ZERO;
		while (d.compareTo(DOUBLE_MAX)>0)
		{
			ret = safeAdd(ret, BIG_DECIMAL_512);
			d = safeDivide(d, BIG_DECIMAL_E_TO_512);
			if (d.compareTo(BigDecimal.ONE)<0) {throw new CrfException("Anomaly");}
			++debug_counter;
		}
		ret = safeAdd(ret, big(Math.log(d.doubleValue())));
		if ( logger.isDebugEnabled() && (debug_counter>0) ) {logger.debug("log() performed "+debug_counter+" iterations.");}
		return ret;
	}
	
//	public static BigDecimal logOld(BigDecimal d, boolean debug_rootCall)
//	{
//		BigDecimal ret = BigDecimal.ZERO;
//		
//		System.out.println("*"+d.toString()+" (scale: "+ d.scale()+ ", ulp: "+d.ulp()+")*");
//		if (d.compareTo(BigDecimal.ZERO)<=0) {throw new CrfException("Tried to calculate log for a non-positive number.");}
//		if (d.compareTo(DOUBLE_MAX)<=0)
//		{
//			ret = big(Math.log(d.doubleValue()));
//		}
//		else
//		{
//			BigDecimal next = safeDivide(d, BIG_DECIMAL_E);
//			System.out.println("d.compareTo(next) = "+d.compareTo(next));
//			if (d.compareTo(next)<=0) {throw new CrfException("<=");}
//			ret = safeAdd(BigDecimal.ONE, log(next, false));
//		}
//		if (debug_rootCall) {System.out.println("log() done.");}
//		return ret;
//	}
	
	
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
	
	private static final Logger logger = Logger.getLogger(DoubleUtilities.class);
}
