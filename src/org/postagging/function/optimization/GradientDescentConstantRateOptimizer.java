package org.postagging.function.optimization;

import org.apache.log4j.Logger;
import org.postagging.function.DerivableFunction;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.StringUtilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class GradientDescentConstantRateOptimizer extends Minimizer<DerivableFunction>
{
	public static final double DEFAULT_RATE = 0.01;
	public static final double DEFAULT_CONVERGENCE_THRESHOLD = 0.0001;
	
	public GradientDescentConstantRateOptimizer(DerivableFunction function)
	{
		this(function,DEFAULT_RATE,DEFAULT_CONVERGENCE_THRESHOLD);
	}
	
	public GradientDescentConstantRateOptimizer(DerivableFunction function,double rate,double convergenceThreshold)
	{
		super(function);
		this.rate = rate;
		this.convergenceThreshold = convergenceThreshold;
	}
	
	
	@Override
	public void find()
	{
		int size = function.size();
		point = new double[size];
		for (int i=0;i<size;++i){point[i]=0.0;}
		
		value = function.value(point);
		double oldValue = value;
		do
		{
			oldValue = value;
			double[] gradient = function.gradient(point);
			for (int i=0;i<size;++i)
			{
				point[i] += rate*(-gradient[i]);
			}
			value = function.value(point);
			if (logger.isDebugEnabled())
			{
				logger.debug(StringUtilities.arrayToString(point)+" = "+String.format("%-3.3f",value));
			}
		}
		while(Math.abs(oldValue-value)>convergenceThreshold);
		calculated = true;
		
	}
	
	
	@Override
	public double getValue()
	{
		if (!calculated) throw new PosTaggerException("Not calculated");
		return value;
	}
	@Override
	public double[] getPoint()
	{
		if (!calculated) throw new PosTaggerException("Not calculated");
		return point;
	}
	
	private final double rate;
	private final double convergenceThreshold;
	
	private boolean calculated = false;
	private double value;
	private double[] point;
	
	
	private static final Logger logger = Logger.getLogger(GradientDescentConstantRateOptimizer.class);
}
