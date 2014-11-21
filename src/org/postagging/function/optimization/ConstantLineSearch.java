package org.postagging.function.optimization;

import org.postagging.function.Function;

/**
 * A simple inexact and inaccurate non-efficient line search which merely returns a small
 * constant for any input.
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class ConstantLineSearch<F extends Function> implements LineSearch<F>
{
	public static final double DEFAULT_RATE = 0.01;
	
	public ConstantLineSearch()
	{
		this(DEFAULT_RATE);
	}
	
	public ConstantLineSearch(double constantRate)
	{
		super();
		this.constantRate = constantRate;
	}


	@Override
	public double findRate(F function, double[] point, double[] direction)
	{
		return constantRate;
	}
	
	
	private final double constantRate;

}
