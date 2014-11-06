package org.postagging.function;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public abstract class TwiceDerivableFunction extends DerivableFunction
{
	public abstract double[][] hessian(double[] point);
}
