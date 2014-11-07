package org.postagging.function.optimization;

import org.postagging.function.DerivableFunction;
import static org.postagging.utilities.VectorUtilities.*;

/**
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class LineSearchUtilities
{
	public static double valueForAlpha(DerivableFunction function, double[] point, double[] direction, double alpha)
	{
		return function.value(addVectors(point, multiplyByScalar(alpha, direction)));
	}
	
	public static double derivationForAlpha(DerivableFunction function, double[] point, double[] direction, double alpha)
	{
		return product(
				function.gradient( addVectors(point, multiplyByScalar(alpha, direction) ) ),
				direction);
	}


}
