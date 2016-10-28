package com.asher_stern.crf.function.optimization;

import static com.asher_stern.crf.utilities.VectorUtilities.*;

import java.math.BigDecimal;

import com.asher_stern.crf.function.DerivableFunction;

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
	public static BigDecimal valueForAlpha(DerivableFunction function, BigDecimal[] point, BigDecimal[] direction, BigDecimal alpha)
	{
		return function.value(addVectors(point, multiplyByScalar(alpha, direction)));
	}

	/**
	 * Returns the (value of the) derivation of f(x+\alpha*d), where both x and d are considered constant vectors, and
	 * the only variable is \alpha. So f(x+\alpha*d) is a function of single variable, and the derivation is derivation
	 * by the variable \alpha.
	 * <br>
	 * The derivation is: f'(x+\alpha*d)*d
	 * 
	 * @return the value of the derivation of f(x+\alpha*d) for the given \alpha.
	 */
	public static BigDecimal derivationForAlpha(DerivableFunction function, BigDecimal[] point, BigDecimal[] direction, BigDecimal alpha)
	{
		return product(
				function.gradient( addVectors(point, multiplyByScalar(alpha, direction) ) ),
				direction);
	}


}
