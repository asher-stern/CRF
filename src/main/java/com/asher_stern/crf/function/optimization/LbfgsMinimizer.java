package com.asher_stern.crf.function.optimization;

import java.math.BigDecimal;
import java.util.ArrayList;
import static com.asher_stern.crf.utilities.ArithmeticUtilities.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.log4j.Logger;

import com.asher_stern.crf.function.DerivableFunction;
import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.DerivableFunctionWithLastCache;
import com.asher_stern.crf.utilities.StringUtilities;
import com.asher_stern.crf.utilities.VectorUtilities;

/**
 * Implementation of L-BFGS algorithm for minimizing a function.
 * <BR>
 * L-BFGS stands for "Limited memory BFGS", where "BFGS" is an acronym of
 * "Broyden Fletcher Goldfarb Shanno" who developed the BFGS algorithm.
 * <BR>
 * The BFGS algorithm approximates Newton method for optimization, by approximating
 * the inverse of the Hessian without calculating the exact Hessian.
 * The L-BFGS algorithm approximates the BFGS algorithm by approximating calculations
 * that are performed with the inverse of the Hessian, but stores neither the
 * inverse of the Hessian nor its approximation in the memory.
 * Thus the L-BFGS algorithm is much cheaper in space complexity notion.
 * <BR>
 * The L-BFGS algorithm is described in the book "Numerical Optimization" by Jorge Nocedal and Stephen J. Wright,
 * Chapter 9. The book can be downloaded from http://www.bioinfo.org.cn/~wangchao/maa/Numerical_Optimization.pdf 
 *  
 *  
 * 
 * @author Asher Stern
 * Date: Nov 7, 2014
 *
 */
public class LbfgsMinimizer extends Minimizer<DerivableFunction>
{
	public static final int DEFAULT_NUMBER_OF_PREVIOUS_ITERATIONS_TO_MEMORIZE = 20;
	public static final BigDecimal DEFAULT_GRADIENT_CONVERGENCE = big(0.01);

	public LbfgsMinimizer(DerivableFunction function)
	{
		this(function,DEFAULT_NUMBER_OF_PREVIOUS_ITERATIONS_TO_MEMORIZE, DEFAULT_GRADIENT_CONVERGENCE);
	}

	public LbfgsMinimizer(DerivableFunction function, int numberOfPreviousIterationsToMemorize, BigDecimal convergence)
	{
//		super(function);
		super(new DerivableFunctionWithLastCache(function));
		this.numberOfPreviousIterationsToMemorize = numberOfPreviousIterationsToMemorize;
		this.convergence = convergence;
		this.convergenceSquare = safeMultiply(this.convergence,this.convergence);
	}
	
	public void setInitialPoint(BigDecimal[] initialPoint)
	{
		if (initialPoint.length!=function.size()) throw new CrfException("Wrong length of initial point specified by the caller.");
		this.initialPoint = initialPoint;
	}
	

	public void setDebugInfo(DebugInfo debugInfo)
	{
		this.debugInfo = debugInfo;
	}

	@Override
	public void find()
	{
		previousItrations = new LinkedList<PointAndGradientSubstractions>();
		LineSearch<DerivableFunction> lineSearch = new ArmijoLineSearch<DerivableFunction>();
		
		initializeInitialPoint();
		value = function.value(point);
		if (logger.isInfoEnabled()) {logger.info("LBFGS: initial value = "+StringUtilities.bigDecimalToString(value));}
		BigDecimal[] gradient = function.gradient(point);
		BigDecimal previousValue = value;
		int forLogger_iterationIndex=0;
		while (VectorUtilities.euclideanNormSquare(gradient).compareTo(convergenceSquare)>0)
		{
			if (logger.isDebugEnabled()) {logger.debug(String.format("Gradient norm square = %s", StringUtilities.bigDecimalToString(VectorUtilities.euclideanNormSquare(gradient)) ));}
			previousValue = value;
			BigDecimal[] previousPoint = Arrays.copyOf(point, point.length);
			BigDecimal[] previousGradient = Arrays.copyOf(gradient, gradient.length);

			// 1. Update point (which is the vector "x").
			
			BigDecimal[] direction = VectorUtilities.multiplyByScalar(BigDecimal.ONE.negate(), twoLoopRecursion(point));
			BigDecimal alpha_rate = lineSearch.findRate(function, point, direction);
			point = VectorUtilities.addVectors(point, VectorUtilities.multiplyByScalar(alpha_rate, direction));
			
			// 2. Prepare next iteration
			value = function.value(point);
			gradient = function.gradient(point);

			previousItrations.add(new PointAndGradientSubstractions(VectorUtilities.subtractVectors(point, previousPoint), VectorUtilities.subtractVectors(gradient, previousGradient)));
			if (previousItrations.size()>numberOfPreviousIterationsToMemorize)
			{
				previousItrations.removeFirst();
			}
			if (previousItrations.size()>numberOfPreviousIterationsToMemorize) {throw new CrfException("BUG");}

			
			// 3. Print log messages
			++forLogger_iterationIndex;
			if (value.compareTo(previousValue)>0) {logger.error("LBFGS: value > previous value");}
			if (logger.isInfoEnabled()) {logger.info("LBFGS iteration "+forLogger_iterationIndex+": value = "+StringUtilities.bigDecimalToString(value)  );}
			if ( (debugInfo!=null) && (logger.isInfoEnabled()) )
			{
				logger.info(debugInfo.info(point));
			}
		}
		calculated = true;
	}

	@Override
	public BigDecimal getValue()
	{
		if (!calculated) {throw new CrfException("Not calculated.");}
		return value;
	}

	@Override
	public BigDecimal[] getPoint()
	{
		if (!calculated) {throw new CrfException("Not calculated.");}
		return point;
	}
	
	
	
	public static interface DebugInfo
	{
		public String info(BigDecimal[] point);
	}
	
	
	
	private void initializeInitialPoint()
	{
		point = new BigDecimal[function.size()];
		if (this.initialPoint==null)
		{
			for (int i=0;i<point.length;++i) {point[i]=BigDecimal.ZERO;}
		}
		else
		{
			if (this.initialPoint.length!=point.length) throw new CrfException("Wrong length of initial point specified by the caller.");
			for (int i=0;i<point.length;++i) {point[i]=this.initialPoint[i];}
		}
	}

	
	private BigDecimal[] twoLoopRecursion(BigDecimal[] point)
	{
		ArrayList<BigDecimal> rhoList = new ArrayList<BigDecimal>(previousItrations.size());
		ArrayList<BigDecimal> alphaList = new ArrayList<BigDecimal>(previousItrations.size());
		
		BigDecimal[] q = function.gradient(point);
		for (PointAndGradientSubstractions substractions : previousItrations)
		{
			BigDecimal rho = safeDivide(BigDecimal.ONE, VectorUtilities.product(substractions.getGradientSubstraction(), substractions.getPointSubstraction()));
			rhoList.add(rho);
			BigDecimal alpha = safeMultiply(rho, VectorUtilities.product(substractions.getPointSubstraction(), q));
			alphaList.add(alpha);
			
			q = VectorUtilities.subtractVectors(q, VectorUtilities.multiplyByScalar(alpha, substractions.getGradientSubstraction()) );
		}
		
		BigDecimal[] r = calculateInitial_r_forTwoLoopRecursion(q);

		ListIterator<PointAndGradientSubstractions> previousIterationsIterator = previousItrations.listIterator(previousItrations.size());
		ListIterator<BigDecimal> rhoIterator = rhoList.listIterator(rhoList.size());
		ListIterator<BigDecimal> alphaIterator = alphaList.listIterator(alphaList.size());
		while (previousIterationsIterator.hasPrevious()&&rhoIterator.hasPrevious()&&alphaIterator.hasPrevious())
		{
			PointAndGradientSubstractions substractions = previousIterationsIterator.previous();
			BigDecimal rho = rhoIterator.previous();
			BigDecimal alpha = alphaIterator.previous();
			
			BigDecimal beta = safeMultiply(rho, VectorUtilities.product(substractions.getGradientSubstraction(), r));
			r = VectorUtilities.addVectors( r, VectorUtilities.multiplyByScalar(safeSubtract(alpha,beta) , substractions.getPointSubstraction()) );
		}
		if ((previousIterationsIterator.hasPrevious()||rhoIterator.hasPrevious()||alphaIterator.hasPrevious())) {throw new CrfException("BUG");}
		
		return r;
	}
	
	
	private BigDecimal[] calculateInitial_r_forTwoLoopRecursion(BigDecimal[] q)
	{
		BigDecimal gamma = BigDecimal.ONE;
		if (previousItrations.size()>=1)
		{
			PointAndGradientSubstractions lastSubstraction = previousItrations.get(0);
			gamma = safeDivide(
					VectorUtilities.product(lastSubstraction.getPointSubstraction(), lastSubstraction.getGradientSubstraction())
					,
					VectorUtilities.product(lastSubstraction.getGradientSubstraction(), lastSubstraction.getGradientSubstraction())
					);
		}
		
		BigDecimal[] r = VectorUtilities.multiplyByScalar(gamma, q);
		return r;
	}
	

	

	// input
	private final int numberOfPreviousIterationsToMemorize; // m
	private final BigDecimal convergence;
	private final BigDecimal convergenceSquare;
	
	private BigDecimal[] initialPoint = null;
	private DebugInfo debugInfo = null;
	
	// internals
	private LinkedList<PointAndGradientSubstractions> previousItrations; // newest is pushed to the beginning.
	private boolean calculated = false;
	
	// output
	private BigDecimal[] point = null;
	private BigDecimal value = BigDecimal.ZERO;
	
	
	


	private static final Logger logger = Logger.getLogger(LbfgsMinimizer.class);
}
