package org.postagging.function;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public abstract class DerivableFunction extends Function
{
	public abstract double[] gradient(double[] point);
}
