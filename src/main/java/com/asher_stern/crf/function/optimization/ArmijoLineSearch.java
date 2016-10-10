package com.asher_stern.crf.function.optimization;

import static com.asher_stern.crf.function.optimization.LineSearchUtilities.derivationForAlpha;
import static com.asher_stern.crf.function.optimization.LineSearchUtilities.valueForAlpha;

import com.asher_stern.crf.function.DerivableFunction;
import com.asher_stern.crf.utilities.CrfException;

/**
 * The Armijo line search is a relatively efficient inexact line search method.
 * <BR>
 * More information about Armijo line search can be found at the <B>Hebrew</B> notebook
 * about Optimization computational methods - theory and exercises notebook by
 * Dori Peleg for Technion course 046197 version 3.0, page 65.
 * <BR>
 * The notebook can be downloaded from here: http://webee.technion.ac.il/people/dorip/optimization%20book.pdf
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class ArmijoLineSearch<F extends DerivableFunction> implements LineSearch<F>
{
	public static final double DEFAULT_BETA_RATE_OF_ALPHA = 0.2;
	public static final double DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT = 0.3;
	public static final double DEFAULT_INITIAL_ALPHA = 1.0; // = 0.01;
	
	public static final double MINIMUM_ALLOWED_ALPHA_VALUE_SO_SHOULD_BE_ZERO = 0.000001;

	@Override
	public double findRate(final F function, final double[] point, final double[] direction)
	{
		final double valueForAlphaZero = valueForAlpha(function, point, direction, 0.0);
		final double derivationForAlphaZero = derivationForAlpha(function, point, direction, 0.0);
		InfinityChecker.check(valueForAlphaZero, derivationForAlphaZero); // If we get infinity here, the initialAlpha is also invalid.
		// In the rest of this function, infinity will cause the while condition to be false, and the function will return with some reasonable alpha.
		
		if (derivationForAlphaZero>=0.0) throw new CrfException("Tried to perform a line search, in a point and direction in which the function does not decrease.");
		
		double ret = 0.0;
		
		double alpha = initialAlpha;
		
		if ( (valueForAlpha(function, point, direction, alpha)-valueForAlphaZero) < sigma_convergenceCoefficient*alpha*derivationForAlphaZero)
		{
			double previousAlpha = alpha;
			do
			{
				previousAlpha = alpha;
				alpha = previousAlpha/beta_rateOfAlpha;
				if (Double.isInfinite(sigma_convergenceCoefficient*alpha*derivationForAlphaZero)) {break;}
			}
			while( (valueForAlpha(function, point, direction, alpha)-valueForAlphaZero) < sigma_convergenceCoefficient*alpha*derivationForAlphaZero);
			ret = previousAlpha;
		}
		else
		{
			do
			{
				alpha = beta_rateOfAlpha*alpha;
				if (alpha<=MINIMUM_ALLOWED_ALPHA_VALUE_SO_SHOULD_BE_ZERO)
				{
					alpha = 0.0;
					break;
				}
				if (Double.isInfinite(sigma_convergenceCoefficient*alpha*derivationForAlphaZero)) {break;}
			}
			while ( (valueForAlpha(function, point, direction, alpha)-valueForAlphaZero) >= sigma_convergenceCoefficient*alpha*derivationForAlphaZero);
			ret = alpha;
		}
		
		return ret;
	}
	
	private final double beta_rateOfAlpha = DEFAULT_BETA_RATE_OF_ALPHA;
	private final double sigma_convergenceCoefficient = DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT;
	private final double initialAlpha = DEFAULT_INITIAL_ALPHA;
}
