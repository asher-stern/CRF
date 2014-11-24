package org.crf.function.optimization;

import org.crf.function.Function;

/**
 * Finds the "rate" by which "moving" the given point towards the given direction minimizes the given function.<BR>
 * More concretely, we are given a multivariate function, f(x), and we are given a point, x, and a direction, d.
 * The goal is to find <B>scalar</B> \alpha>0, such that f(x+\alpha*d) is minimized.
 * <P>
 * <I>Exact line search</I> finds exactly the \alpha which minimizes f(x+\alpha*d).<BR>
 * <I>Inexact line search</I> finds an \alpha which yields a value that is "quite close" to the minimum.
 * 
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 * @param <F>
 */
public interface LineSearch<F extends Function>
{
	/**
	 * Finds the "rate" by which "moving" the given point towards the given direction minimizes the given function.
	 * More concretely, finds \alpha, such that f(x+\alpha*d) is minimized, or at least "quite close" to be minimized.
	 * @param function a multivariate function
	 * @param point "point" is "x" -- the input for the function.
	 * @param direction a vector, of the same dimension of x (point), termed above "d" for which we want to find the \alpha.
	 * @return the \alpha -- the rate which minimizes (or nearly minimizes) the value of f(x+\alpha*d).
	 */
	public double findRate(F function, double[] point, double[] direction);
}
