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
	
	public GradientDescentOptimizer(DerivableFunction function)
	{
		this(function,DEFAULT_RATE,DEFAULT_CONVERGENCE_THRESHOLD);
	}
	
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
			for (int i=0;i<size;++i)
			{
				point[i] += actualRate*(-gradient[i]);
			}
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
	
	@SuppressWarnings("unused")
	private final double rate;
	private final double convergenceThreshold;
	
	private boolean calculated = false;
	private double value;
	private double[] point;
	
	
	private static final Logger logger = Logger.getLogger(GradientDescentOptimizer.class);
}
