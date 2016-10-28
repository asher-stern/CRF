package com.asher_stern.crf.function;

import java.math.BigDecimal;

/**
 * A multivariate function. A function f(x), where x is a vector, and the function returns a scalar.
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public abstract class Function
{
	/**
	 * Returns the f(x) -- the value of the function in the given x.
	 * @param point the "point" is x -- the input for the function.
	 * @return the value of f(x)
	 */
	public abstract BigDecimal value(BigDecimal[] point);
	
	/**
	 * The size (dimension) of the input vector (x).
	 * @return the size (dimension) of the input vector (x).
	 */
	public abstract int size();
}
