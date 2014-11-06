package org.postagging.function.optimization;

import org.postagging.function.Function;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 * @param <F>
 */
public abstract class Optimizer<F extends Function>
{
	public Optimizer(F function)
	{
		this.function = function;
	}
	
	public abstract void find();
	
	public abstract double getValue();
	
	public abstract double[] getPoint();

	protected final F function;
}
