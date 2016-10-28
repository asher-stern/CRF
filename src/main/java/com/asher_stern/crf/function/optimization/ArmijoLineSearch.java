package com.asher_stern.crf.function.optimization;

import static com.asher_stern.crf.function.optimization.LineSearchUtilities.derivationForAlpha;
import static com.asher_stern.crf.function.optimization.LineSearchUtilities.valueForAlpha;
import static com.asher_stern.crf.utilities.DoubleUtilities.safeDivide;
import static com.asher_stern.crf.utilities.DoubleUtilities.safeMultiply;
import static com.asher_stern.crf.utilities.DoubleUtilities.safeSubtract;

import java.math.BigDecimal;

import com.asher_stern.crf.function.DerivableFunction;
import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.DoubleUtilities;

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
	public static final BigDecimal DEFAULT_BETA_RATE_OF_ALPHA = new BigDecimal(0.2, DoubleUtilities.MC);
	public static final BigDecimal DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT = new BigDecimal(0.3, DoubleUtilities.MC);
	public static final BigDecimal DEFAULT_INITIAL_ALPHA = new BigDecimal(1.0, DoubleUtilities.MC); // = 0.01;
	
	public static final BigDecimal MINIMUM_ALLOWED_ALPHA_VALUE_SO_SHOULD_BE_ZERO = new BigDecimal(0.000001, DoubleUtilities.MC);

	@Override
	public BigDecimal findRate(final F function, final BigDecimal[] point, final BigDecimal[] direction)
	{
		final BigDecimal valueForAlphaZero = valueForAlpha(function, point, direction, BigDecimal.ZERO);
		final BigDecimal derivationForAlphaZero = derivationForAlpha(function, point, direction, BigDecimal.ZERO);
		
		if (derivationForAlphaZero.compareTo(BigDecimal.ZERO) >=0) throw new CrfException("Tried to perform a line search, in a point and direction in which the function does not decrease.");
		
		BigDecimal ret = BigDecimal.ZERO;
		
		BigDecimal alpha = initialAlpha;
		
		
		
		if (safeSubtract(valueForAlpha(function, point, direction, alpha), valueForAlphaZero).compareTo(safeMultiply(sigma_convergenceCoefficient, alpha, derivationForAlphaZero))<0)
		{
			BigDecimal previousAlpha = alpha;
			do
			{
				previousAlpha = alpha;
				alpha = safeDivide(previousAlpha, beta_rateOfAlpha);
			}
			while( safeSubtract(valueForAlpha(function, point, direction, alpha),valueForAlphaZero).compareTo(safeMultiply(sigma_convergenceCoefficient, alpha, derivationForAlphaZero))<0);
			ret = previousAlpha;
		}
		else
		{
			do
			{
				alpha = safeMultiply(beta_rateOfAlpha, alpha);
				if (alpha.compareTo(MINIMUM_ALLOWED_ALPHA_VALUE_SO_SHOULD_BE_ZERO) <= 0)
				{
					alpha = BigDecimal.ZERO;
					break;
				}
			}
			while ( safeSubtract(valueForAlpha(function, point, direction, alpha), valueForAlphaZero).compareTo(safeMultiply(sigma_convergenceCoefficient, alpha, derivationForAlphaZero)) >= 0);
			ret = alpha;
		}
		
		return ret;
	}
	
	private final BigDecimal beta_rateOfAlpha = DEFAULT_BETA_RATE_OF_ALPHA;
	private final BigDecimal sigma_convergenceCoefficient = DEFAULT_SIGMA_CONVERGENCE_COEFFICIENT;
	private final BigDecimal initialAlpha = DEFAULT_INITIAL_ALPHA;
}
