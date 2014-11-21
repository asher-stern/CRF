package org.postagging.smalltests;

import org.apache.log4j.Level;
import org.postagging.function.DerivableFunction;
import org.postagging.function.optimization.LbfgsMinimizer;
import org.postagging.function.optimization.Minimizer;
import org.postagging.utilities.StringUtilities;
import org.postagging.utilities.log4j.Log4jInit;

/**
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class DemoOptimizer
{

	public static void main(String[] args)
	{
		try
		{
			Log4jInit.init(Level.DEBUG);
			new DemoOptimizer().go();			
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}

	}
	

	public void go()
	{
		DerivableFunction function = createFunction();
		//Optimizer<?> optimizer = new GradientDescentOptimizer(function);
		Minimizer<?> optimizer = new LbfgsMinimizer(function);
		optimizer.find();
		
		System.out.println("point = "+StringUtilities.arrayOfDoubleToString(optimizer.getPoint()));
		System.out.println("value = "+String.format("%-3.3f",optimizer.getValue()));
	}
	
//	private DerivableFunction createFunction()
//	{
//		// (x+2)^2
//		DerivableFunction function = new DerivableFunction()
//		{
//			@Override
//			public double value(double[] point)
//			{
//				return (point[0]+2.0)*(point[0]+2.0);
//			}
//			
//			@Override
//			public int size()
//			{
//				return 1;
//			}
//			
//			@Override
//			public double[] gradient(double[] point)
//			{
//				return new double[]{2.0*(point[0]+2.0)};
//			}
//		};
//		
//		return function;
//	}

	
	
	private DerivableFunction createFunction()
	{
		// (x_1-2)^2 + (2-x_2)^2 + x_1*x_2
		DerivableFunction function = new DerivableFunction()
		{
			@Override
			public double value(double[] point)
			{
				return (point[0]-2.0)*(point[0]-2.0)+(2.0-point[1])*(2.0-point[1])+point[0]*point[1];
			}
			
			@Override
			public int size()
			{
				return 2;
			}
			
			@Override
			public double[] gradient(double[] point)
			{
				return new double[]{2.0*(point[0]-2.0)+point[1], -2.0*(2.0-point[1])+point[0]};
			}
		};
		
		return function;
	}

}
