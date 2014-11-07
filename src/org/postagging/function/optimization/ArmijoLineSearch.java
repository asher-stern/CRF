package org.postagging.function.optimization;

import org.postagging.function.DerivableFunction;
import org.postagging.utilities.PosTaggerException;

import static org.postagging.function.optimization.LineSearchUtilities.*;

/**
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class ArmijoLineSearch<F extends DerivableFunction> implements LineSearch<F>
{
	public static final double DEFAULT_BETA_RATE_OF_ALPHA = 0.2;
	public static final double DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT = 0.3;
	public static final double DEFAULT_INITIAL_ALPHA = 0.01;

	@Override
	public double findRate(F function, double[] point, double[] direction)
	{
		if (!sanityCheck(function,point,direction)) throw new PosTaggerException("Tried to perform a line search, in a point and direction in which the function does not decrease.");
		double ret = 0.0;
		
		double alpha = initialAlpha;
		
		if ( (valueForAlpha(function, point, direction, alpha)-valueForAlpha(function, point, direction, 0)) < sigma_convergenceCoefficient*alpha*derivationForAlpha(function, point, direction, 0))
		{
			double previousAlpha = alpha;
			do
			{
				previousAlpha = alpha;
				alpha = previousAlpha/beta_rateOfAlpha;
			}
			while( (valueForAlpha(function, point, direction, alpha)-valueForAlpha(function, point, direction, 0)) < sigma_convergenceCoefficient*alpha*derivationForAlpha(function, point, direction, 0));
			ret = previousAlpha;
		}
		else
		{
			do
			{
				alpha = beta_rateOfAlpha*alpha;
			}
			while (!( (valueForAlpha(function, point, direction, alpha)-valueForAlpha(function, point, direction, 0)) < sigma_convergenceCoefficient*alpha*derivationForAlpha(function, point, direction, 0)));
			ret = alpha;
		}
		return ret;
	}
	
	private boolean sanityCheck(F function, double[] point, double[] direction)
	{
		return (derivationForAlpha(function, point, direction, 0)<0.0);
	}
	
	
	private final double beta_rateOfAlpha = DEFAULT_BETA_RATE_OF_ALPHA;
	private final double sigma_convergenceCoefficient = DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT;
	private final double initialAlpha = DEFAULT_INITIAL_ALPHA;
}
