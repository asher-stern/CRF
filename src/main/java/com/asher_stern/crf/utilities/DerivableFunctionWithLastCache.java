package com.asher_stern.crf.utilities;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.asher_stern.crf.function.DerivableFunction;

/**
 * A {@link DerivableFunction} that remembers the last computed value and gradient.
 * So, if the user calls {@link #value(double[])} for the same point (and there was
 * no other call to {@link #value(double[])} in between) then the value will no be computed twice. Rather, the value
 * computed by the first call is stored, and will be returned.
 * The same applies to {@link #gradient(double[])}.
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 */
public class DerivableFunctionWithLastCache extends DerivableFunction
{
	public DerivableFunctionWithLastCache(DerivableFunction realFunction)
	{
		super();
		this.realFunction = realFunction;
	}

	@Override
	public BigDecimal value(BigDecimal[] point)
	{
		double ret = 0.0;
		DoubleArrayWrapper wrappedPoint = new DoubleArrayWrapper(point);
		Double fromCache = valueCache.get(wrappedPoint);
		if (null==fromCache)
		{
			double calculatedValue = realFunction.value(point);
			valueCache.put(wrappedPoint,calculatedValue);
			ret = calculatedValue;
		}
		else
		{
			logger.debug("Returning value from cache");
			ret = fromCache;
		}
		return ret;
	}

	@Override
	public double[] gradient(double[] point)
	{
		double[] ret = null;
		DoubleArrayWrapper wrappedPoint = new DoubleArrayWrapper(point);
		double[] fromCache = gradientCache.get(wrappedPoint);
		if (null==fromCache)
		{
			double[] calculatedGradient = realFunction.gradient(point);
			gradientCache.put(wrappedPoint, calculatedGradient);
			ret = calculatedGradient;
		}
		else
		{
			logger.debug("Returning gradient from cache");
			ret = fromCache;
		}
		return ret;
	}


	@Override
	public int size()
	{
		return realFunction.size();
	}

	
	private final LastCache<DoubleArrayWrapper, Double> valueCache = new LastCache<DoubleArrayWrapper, Double>();
	private final LastCache<DoubleArrayWrapper, double[]> gradientCache = new LastCache<DoubleArrayWrapper, double[]>();
	
	private final DerivableFunction realFunction;
	
	private static final Logger logger = Logger.getLogger(DerivableFunctionWithLastCache.class);
}
