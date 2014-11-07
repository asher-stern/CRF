package org.postagging.utilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class VectorUtilities
{
	public static double product(double[] rowVector, double[] columnVector)
	{
		if (rowVector.length!=columnVector.length) throw new PosTaggerException("Cannot multiply vector of different sizes.");
		double ret = 0.0;
		for (int i=0;i<rowVector.length;++i)
		{
			ret += rowVector[i]*columnVector[i];
		}
		return ret;
	}
	
	public static double[] multiplyByScalar(double scalar, double[] vector)
	{
		double[] ret = new double[vector.length];
		for (int i=0;i<vector.length;++i)
		{
			ret[i] = scalar*vector[i];
		}
		return ret;
	}
	
	public static double[] addVectors(double[] vector1, double[] vector2)
	{
		if (vector1.length!=vector2.length) throw new PosTaggerException("Cannot add two vectors of different sizes.");
		double[] ret = new double[vector1.length];
		for (int i=0;i<vector1.length;++i)
		{
			ret[i]=vector1[i]+vector2[i];
		}
		return ret;
	}
	
	public static double[] substractVectors(double[] vector1, double[] vector2)
	{
		if (vector1.length!=vector2.length) throw new PosTaggerException("Cannot substract vectors of difference sizes.");
		double[] ret = new double[vector1.length];
		for (int i=0;i<vector1.length;++i)
		{
			ret[i] = vector1[i]-vector2[i];
		}
		return ret;
	}
}
