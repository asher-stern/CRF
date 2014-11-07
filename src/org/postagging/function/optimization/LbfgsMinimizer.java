package org.postagging.function.optimization;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.log4j.Logger;
import org.postagging.function.DerivableFunction;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.VectorUtilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class LbfgsMinimizer extends Minimizer<DerivableFunction>
{
	public static final int DEFAULT_NUMBER_OF_PREVIOUS_ITERATIONS_TO_MEMORIZE = 20;
	public static final double DEFAULT_CONVERGENCE = 0.001;

	public LbfgsMinimizer(DerivableFunction function)
	{
		this(function,DEFAULT_NUMBER_OF_PREVIOUS_ITERATIONS_TO_MEMORIZE,DEFAULT_CONVERGENCE);
	}

	public LbfgsMinimizer(DerivableFunction function, int numberOfPreviousIterationsToMemorize, double convergence)
	{
		super(function);
		this.numberOfPreviousIterationsToMemorize = numberOfPreviousIterationsToMemorize;
		this.convergence = convergence;
	}

	@Override
	public void find()
	{
		previousItrations = new LinkedList<PointAndGradientSubstractions>();
		LineSearch<DerivableFunction> lineSearch = new ArmijoLineSearch<DerivableFunction>();
		
		point = new double[function.size()];
		for (int i=0;i<point.length;++i) {point[i]=0.0;}
		value = function.value(point);
		double[] gradient = function.gradient(point);
		double previousValue = value;
		int debug_iterationIndex=0;
		do
		{
			previousValue = value;
			double[] previousPoint = point;
			double[] previousGradient = gradient;
			
			double[] direction = VectorUtilities.multiplyByScalar(-1.0, twoLoopRecursion(point));
			double alpha_rate = lineSearch.findRate(function, point, direction);
			point = VectorUtilities.addVectors(point, VectorUtilities.multiplyByScalar(alpha_rate, direction));
			value = function.value(point);
			gradient = function.gradient(point);
			
			previousItrations.add(new PointAndGradientSubstractions(VectorUtilities.substractVectors(point, previousPoint), VectorUtilities.substractVectors(gradient, previousGradient)));
			if (previousItrations.size()>numberOfPreviousIterationsToMemorize)
			{
				previousItrations.removeLast();
			}
			if (previousItrations.size()>numberOfPreviousIterationsToMemorize) {throw new PosTaggerException("BUG");}
			
			if (value>previousValue) {logger.warn("LBFGS: value > previous value");}
			++debug_iterationIndex;
			if (logger.isDebugEnabled()) {logger.debug("LBFGS iteration: "+debug_iterationIndex);}
		}
		while(Math.abs(previousValue-value)>convergence);
		
		calculated = true;
	}

	@Override
	public double getValue()
	{
		if (!calculated) {throw new PosTaggerException("Not calculated.");}
		return value;
	}

	@Override
	public double[] getPoint()
	{
		if (!calculated) {throw new PosTaggerException("Not calculated.");}
		return point;
	}
	
	
	
	private double[] twoLoopRecursion(double[] point)
	{
		ArrayList<Double> rhoList = new ArrayList<Double>(previousItrations.size());
		ArrayList<Double> alphaList = new ArrayList<Double>(previousItrations.size());
		
		double[] q = function.gradient(point);
		for (PointAndGradientSubstractions substractions : previousItrations)
		{
			double rho = 1.0/VectorUtilities.product(substractions.getGradientSubstraction(), substractions.getPointSubstraction());
			rhoList.add(rho);
			double alpha = rho*VectorUtilities.product(substractions.getPointSubstraction(), q);
			alphaList.add(alpha);
			
			q = VectorUtilities.substractVectors(q, VectorUtilities.multiplyByScalar(alpha, substractions.getGradientSubstraction()) );
		}
		
		double[] r = calculateInitial_r_forTwoLoopRecursion(q);

		ListIterator<PointAndGradientSubstractions> previousIterationsIterator = previousItrations.listIterator(previousItrations.size());
		ListIterator<Double> rhoIterator = rhoList.listIterator(rhoList.size());
		ListIterator<Double> alphaIterator = alphaList.listIterator(alphaList.size());
		while (previousIterationsIterator.hasPrevious()&&rhoIterator.hasPrevious()&&alphaIterator.hasPrevious())
		{
			PointAndGradientSubstractions substractions = previousIterationsIterator.previous();
			double rho = rhoIterator.previous();
			double alpha = alphaIterator.previous();
			
			double betta = rho * VectorUtilities.product(substractions.getGradientSubstraction(), r);
			r = VectorUtilities.addVectors( r, VectorUtilities.multiplyByScalar(alpha-betta, substractions.getPointSubstraction()) );
		}
		if ((previousIterationsIterator.hasPrevious()||rhoIterator.hasPrevious()||alphaIterator.hasPrevious())) {throw new PosTaggerException("BUG");}
		
		return r;
	}
	
	
	private double[] calculateInitial_r_forTwoLoopRecursion(double[] q)
	{
		double gamma = 1.0;
		if (previousItrations.size()>=1)
		{
			PointAndGradientSubstractions lastSubstraction = previousItrations.get(0);
			gamma =
					VectorUtilities.product(lastSubstraction.getPointSubstraction(), lastSubstraction.getGradientSubstraction())
					/
					VectorUtilities.product(lastSubstraction.getGradientSubstraction(), lastSubstraction.getGradientSubstraction());
			
			
		}
		
		double[] r = VectorUtilities.multiplyByScalar(gamma, q);
		return r;
	}
	

	

	
	
	private final int numberOfPreviousIterationsToMemorize; // m
	private final double convergence;
	
	private LinkedList<PointAndGradientSubstractions> previousItrations; // newest is pushed to the beginning.
	
	private double[] point = null;
	private double value = 0.0;
	
	private boolean calculated = false;
	


	private static final Logger logger = Logger.getLogger(LbfgsMinimizer.class);
}
