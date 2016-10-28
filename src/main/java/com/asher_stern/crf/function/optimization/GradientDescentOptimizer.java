package com.asher_stern.crf.function.optimization;

import static com.asher_stern.crf.utilities.DoubleUtilities.big;
import static com.asher_stern.crf.utilities.DoubleUtilities.safeAdd;
import static com.asher_stern.crf.utilities.DoubleUtilities.safeMultiply;
import static com.asher_stern.crf.utilities.DoubleUtilities.safeSubtract;

import java.math.BigDecimal;

import org.apache.log4j.Logger;

import com.asher_stern.crf.function.DerivableFunction;
import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.StringUtilities;
import com.asher_stern.crf.utilities.VectorUtilities;

/**
 * A {@link Minimizer} which updates the function's input by moving it along the negation of its
 * gradient.
 * This method is simple but <B> inefficient</B>.
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class GradientDescentOptimizer extends Minimizer<DerivableFunction>
{
	public static final BigDecimal DEFAULT_RATE = big(0.01);
	public static final BigDecimal DEFAULT_CONVERGENCE_THRESHOLD = big(0.0001);
	
	/**
	 * Constructor with default convergence threshold. See {@link GradientDescentOptimizer#GradientDescentOptimizer(DerivableFunction, double, double)}.
	 * @param function
	 */
	public GradientDescentOptimizer(DerivableFunction function)
	{
		this(function,DEFAULT_RATE,DEFAULT_CONVERGENCE_THRESHOLD);
	}
	
	/**
	 * Constructor with convergence threshold.
	 * @param function the function to optimize (find its minimum).
	 * @param rate <b>Not used in this implementation</b>. Rate is a coefficient by which the gradient is multiplied at
	 * each step of the gradient descent. However, a more advanced technique is to use Armijo line search
	 * (see {@link ArmijoLineSearch}) which find a good rate automatically.
	 * For developers: it is possible to change the code and use {@link ConstantLineSearch}, and use this given rate. 
	 * @param convergenceThreshold the convergence threshold, which is the maximum allowed gap between the result of this
	 * optimizer and the real optimum (i.e., the optimizer might return a result which is only "close enough" to the optimum,
	 * while being slightly different from the real optimum).
	 */
	public GradientDescentOptimizer(DerivableFunction function,BigDecimal rate,BigDecimal convergenceThreshold)
	{
		super(function);
		this.rate = rate;
		this.convergenceThreshold = convergenceThreshold;
	}
	
	
	@Override
	public void find()
	{
//		LineSearch<DerivableFunction> lineSearch = new ConstantLineSearch<DerivableFunction>(rate);
		LineSearch<DerivableFunction> lineSearch = new ArmijoLineSearch<DerivableFunction>();
		
		int size = function.size();
		point = new BigDecimal[size];
		for (int i=0;i<size;++i){point[i]=BigDecimal.ZERO;}
		
		value = function.value(point);
		BigDecimal oldValue = value;
		int debug_iterationIndex=0;
		do
		{
			oldValue = value;
			BigDecimal[] gradient = function.gradient(point);
			BigDecimal actualRate = lineSearch.findRate(function, point, VectorUtilities.multiplyByScalar(BigDecimal.ONE.negate(), gradient));
			singleStepUpdate(size, point, gradient, actualRate);
			value = function.value(point);
			if (logger.isDebugEnabled())
			{
				logger.debug(StringUtilities.arrayOfBigDecimalToString(point)+" = "+String.format("%-3.3f",value));
			}
			++debug_iterationIndex;
		}
		while(safeSubtract(oldValue,value).abs().compareTo(convergenceThreshold) > 0);
		if (logger.isDebugEnabled()){logger.debug("Gradient-descent: number of iterations: "+debug_iterationIndex);}
		calculated = true;
		
	}
	
	
	@Override
	public BigDecimal getValue()
	{
		if (!calculated) throw new CrfException("Not calculated");
		return value;
	}
	@Override
	public BigDecimal[] getPoint()
	{
		if (!calculated) throw new CrfException("Not calculated");
		return point;
	}
	
	
	public static final void singleStepUpdate(final int size, final BigDecimal[] point, BigDecimal[] gradient, final BigDecimal rate)
	{
		// size must be equal to point.length 
		for (int i=0;i<size;++i)
		{
			point[i] = safeAdd(point[i], safeMultiply(rate, gradient[i].negate()));
		}
	}
	
	
	
	@SuppressWarnings("unused")
	private final BigDecimal rate;
	private final BigDecimal convergenceThreshold;
	
	private boolean calculated = false;
	private BigDecimal value;
	private BigDecimal[] point;
	
	
	private static final Logger logger = Logger.getLogger(GradientDescentOptimizer.class);
}
