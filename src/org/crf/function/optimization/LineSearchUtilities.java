package org.crf.function.optimization;

import org.crf.function.DerivableFunction;

import static org.crf.utilities.VectorUtilities.*;

/**
 * A collection of static helper functions, needed for some implementations of {@link LineSearch}.
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class LineSearchUtilities
{
	/**
	 * Returns f(x+\alpha*d), where "x" is the given point, "d" is the given direction, and "\alpha" is some scalar.
	 */
	public static double valueForAlpha(DerivableFunction function, double[] point, double[] direction, double alpha)
	{
		return function.value(addVectors(point, multiplyByScalar(alpha, direction)));
	}

	/**
	 * Returns the (value of the) derivation of f(x+\alpha*d), where both x and d are considered constant vectors, and
	 * the only variable is \alpha. So f(x+\alpha*d) is a function of single variable, and the derivation is derivation
	 * by the variable \alpha.
	 * @return the value of the derivation of f(x+\alpha*d) for the given \alpha.
	 */
	public static double derivationForAlpha(DerivableFunction function, double[] point, double[] direction, double alpha)
	{
		return product(
				function.gradient( addVectors(point, multiplyByScalar(alpha, direction) ) ),
				direction);
	}


}
