package com.asher_stern.crf.function.optimization;

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
	public static final double DEFAULT_RATE = 0.01;
	public static final double DEFAULT_CONVERGENCE_THRESHOLD = 0.0001;
	
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
	public GradientDescentOptimizer(DerivableFunction function,double rate,double convergenceThreshold)
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
		point = new double[size];
		for (int i=0;i<size;++i){point[i]=0.0;}
		
		value = function.value(point);
		double oldValue = value;
		int debug_iterationIndex=0;
		do
		{
			oldValue = value;
			double[] gradient = function.gradient(point);
			double actualRate = lineSearch.findRate(function, point, VectorUtilities.multiplyByScalar(-1.0, gradient));
			singleStepUpdate(size, point, gradient, actualRate);
			value = function.value(point);
			if (logger.isDebugEnabled())
			{
				logger.debug(StringUtilities.arrayOfDoubleToString(point)+" = "+String.format("%-3.3f",value));
			}
			++debug_iterationIndex;
		}
		while(Math.abs(oldValue-value)>convergenceThreshold);
		if (logger.isDebugEnabled()){logger.debug("Gradient-descent: number of iterations: "+debug_iterationIndex);}
		calculated = true;
		
	}
	
	
	@Override
	public double getValue()
	{
		if (!calculated) throw new CrfException("Not calculated");
		return value;
	}
	@Override
	public double[] getPoint()
	{
		if (!calculated) throw new CrfException("Not calculated");
		return point;
	}
	
	
	public static final void singleStepUpdate(final int size, final double[] point, final double[] gradient, final double rate)
	{
		changeInfinityToDoubleMax(gradient);
		// size must be equal to point.length 
		for (int i=0;i<size;++i)
		{
			point[i] += rate*(-gradient[i]);
		}
	}
	
	
	private static final void changeInfinityToDoubleMax(double[] array)
	{
		for (int i=0; i<array.length; ++i)
		{
			if (Double.POSITIVE_INFINITY==array[i])
			{
				array[i] = Double.MAX_VALUE;
			}
			else if (Double.NEGATIVE_INFINITY==array[i])
			{
				array[i] = -Double.MAX_VALUE;
			}
		}
	}
	
	@SuppressWarnings("unused")
	private final double rate;
	private final double convergenceThreshold;
	
	private boolean calculated = false;
	private double value;
	private double[] point;
	
	
	private static final Logger logger = Logger.getLogger(GradientDescentOptimizer.class);
}
