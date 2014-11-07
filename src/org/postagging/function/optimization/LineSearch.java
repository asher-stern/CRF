package org.postagging.function.optimization;

import org.postagging.function.Function;

/**
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 * @param <F>
 */
public interface LineSearch<F extends Function>
{
	public double findRate(F function, double[] point, double[] direction);
}
