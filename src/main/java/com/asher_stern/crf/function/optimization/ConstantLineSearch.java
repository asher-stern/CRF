package com.asher_stern.crf.function.optimization;

import java.math.BigDecimal;

import com.asher_stern.crf.function.Function;
import com.asher_stern.crf.utilities.DoubleUtilities;

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
	public static final BigDecimal DEFAULT_RATE = new BigDecimal(0.01, DoubleUtilities.MC);
	
	public ConstantLineSearch()
	{
		this(DEFAULT_RATE);
	}
	
	public ConstantLineSearch(BigDecimal constantRate)
	{
		super();
		this.constantRate = constantRate;
	}


	@Override
	public BigDecimal findRate(F function, BigDecimal[] point, BigDecimal[] direction)
	{
		return constantRate;
	}
	
	
	private final BigDecimal constantRate;

}
