package org.crf.function.optimization;

import org.crf.function.Function;

/**
 * Given a multivariate function, f(x), finds "x" in which the function has its global minimum value.
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 * @param <F>
 */
public abstract class Minimizer<F extends Function>
{
	public Minimizer(F function)
	{
		this.function = function;
	}
	
	/**
	 * Find the vector x, where f(x) is minimized.
	 */
	public abstract void find();
	
	/**
	 * Returns f(x), where x is the vector found by the function {@link #find()}.
	 * @return f(x), where x is the vector found by the function {@link #find()}.
	 */
	public abstract double getValue();
	
	/**
	 * Returns "x" -- the vector found by the function {@link #find()}.
	 * @return "x" -- the vector found by the function {@link #find()}.
	 */
	public abstract double[] getPoint();

	/**
	 * The function for which it is required to find the point in which it gets its minimum.
	 */
	protected final F function;
}
