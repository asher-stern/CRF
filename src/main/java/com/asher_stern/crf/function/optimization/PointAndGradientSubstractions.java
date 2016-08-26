package com.asher_stern.crf.function.optimization;

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
	public PointAndGradientSubstractions(double[] pointSubstraction, double[] gradientSubstraction)
	{
		super();
		this.pointSubstraction = pointSubstraction;
		this.gradientSubstraction = gradientSubstraction;
	}
	
	
	
	public double[] getPointSubstraction()
	{
		return pointSubstraction;
	}
	public double[] getGradientSubstraction()
	{
		return gradientSubstraction;
	}



	private final double[] pointSubstraction;
	private final double[] gradientSubstraction;

}
