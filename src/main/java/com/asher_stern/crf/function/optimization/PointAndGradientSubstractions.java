package com.asher_stern.crf.function.optimization;

import java.math.BigDecimal;

/**
 * Encapsulates the substraction of two given points, and the substraction of the gradient calculated for them (for a given
 * function).
 * This class is used by {@link LbfgsMinimizer}.
 * 
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class PointAndGradientSubstractions
{
	public PointAndGradientSubstractions(BigDecimal[] pointSubstraction, BigDecimal[] gradientSubstraction)
	{
		super();
		this.pointSubstraction = pointSubstraction;
		this.gradientSubstraction = gradientSubstraction;
	}
	
	
	
	public BigDecimal[] getPointSubstraction()
	{
		return pointSubstraction;
	}
	public BigDecimal[] getGradientSubstraction()
	{
		return gradientSubstraction;
	}



	private final BigDecimal[] pointSubstraction;
	private final BigDecimal[] gradientSubstraction;

}
