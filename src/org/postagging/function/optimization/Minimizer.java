package org.postagging.function.optimization;

import org.postagging.function.Function;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 * @param <F>
 */
public abstract class Minimizer<F extends Function> extends Optimizer<F>
{
	public Minimizer(F function)
	{
		super(function);
	}
}
