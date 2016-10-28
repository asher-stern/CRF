package com.asher_stern.crf.function;

import java.math.BigDecimal;

/**
 * A multivariate differentiable function for which we know how to calculate the second derivative for every pair of variables.
 * See {@link Function} and {@link DerivableFunction} about the function and the gradient (which is the first derivative).
 * <BR>
 * The second derivatives are represented as a matrix, named "Hessian". The cell (i,j) in the Hessian is the partial derivative
 * of f by the ith variable, derived again by the jth variable.
 * For example, let f(x_1,x_2) = (x_1)^2 + x_1*x_2.<BR>
 * Its gradient is the vector [2*x_1 + x_2 , x_1].<BR>
 * Its Hessian is
 * <pre>
 * 2 1
 * 1 0
 * </pre>
 * which means:<BR>
 * The partial derivative by x_1 is 2*x_1 + x_2, and its derivation by x_1 again is 2.<BR>
 * The partial derivative by x_1 is 2*x_1 + x_2, and its derivation by x_2 is 1.<BR>
 * The partial derivative by x_2 is x_1, and its derivation by x_1 is 1.<BR>
 * The partial derivative by x_2 is x_1, and its derivation by x_2 is 0.<BR>
 * 
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public abstract class TwiceDerivableFunction extends DerivableFunction
{
	/**
	 * Returns the Hessian matrix for the given function in the given point (point is "x" -- the function input).
	 * @param point "point" is "x" -- the input for the function.
	 * @return the Hessian matrix -- the matrix of partial second derivations.
	 */
	public abstract BigDecimal[][] hessian(BigDecimal[] point);
}
