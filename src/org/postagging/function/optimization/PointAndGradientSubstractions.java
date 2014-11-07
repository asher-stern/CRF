package org.postagging.function.optimization;

/**
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
