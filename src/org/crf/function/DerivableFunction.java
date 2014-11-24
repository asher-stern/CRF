package org.crf.function;

/**
 * A multivariate function, which is differentiable.<BR>
 * We know how to calculate all of its partial derivatives.
 * <P>
 * The vector of the partial derivatives is named "gradient". For example, let f(x_1,x_2) = (x_1)^2 + x_1*x_2
 * Then the partial derivative for x_1 is 2*x_1 + x_2. The partial derivative for x_2 is x_1.
 * Accordingly, the gradient is the vector [2*x_1 + x_2 , x_1].<BR>
 * So, for example, for the point [3,5] the gradient is [2*3+5 , 3] = [11 , 3].
 * <BR>
 * The method {@link #gradient(double[])} returns the gradient in a given point (i.e., x, where x is a vector).
 * 
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public abstract class DerivableFunction extends Function
{
	/**
	 * Returns the gradient of the function in the given point.
	 * For example, if the function is f(x_1,x_2) = (x_1)^2 + x_1*x_2, then for [3,5] the returned gradient is [11 , 3].
	 * @param point the point is "x", the input for the function, for which the user needs the gradient.
	 * @return the gradient of the function in the given point.
	 */
	public abstract double[] gradient(double[] point);
}
