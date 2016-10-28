package com.asher_stern.crf.function.optimization;

import java.math.BigDecimal;

import com.asher_stern.crf.function.DerivableFunction;
import com.asher_stern.crf.function.Function;
import com.asher_stern.crf.function.TwiceDerivableFunction;
import com.asher_stern.crf.utilities.CrfException;

/**
 * Represent "-f(x)" for a given function "f(x)".
 * <BR>
 * This negated function can be used when maximization of a function is needed, but a minimization algorithm is available.
 * Just minimize "-f(x)", and the resulting "x" is the point of the maximum for "f(x)".
 * 
 * @author Asher Stern
 * Date: Nov 6, 2014
 *
 */
public class NegatedFunction extends TwiceDerivableFunction
{
	public static NegatedFunction fromFunction(Function function)
	{
		return new NegatedFunction(function, null, null);
	}
	
	public static NegatedFunction fromDerivableFunction(DerivableFunction derivableFunction)
	{
		return new NegatedFunction(null,derivableFunction,null);
	}
	
	public static NegatedFunction fromTwiceDerivableFunction(TwiceDerivableFunction twiceDerivableFunction)
	{
		return new NegatedFunction(null,null,twiceDerivableFunction);
	}

	
	@Override
	public int size()
	{
		return this.theSize;
	}

	@Override
	public BigDecimal value(BigDecimal[] point)
	{
		
		if (function!=null){return function.value(point).negate();}
		else if (derivableFunction!=null){return derivableFunction.value(point).negate();}
		else if (twiceDerivableFunction!=null){return twiceDerivableFunction.value(point).negate();}
		else throw new CrfException("BUG");
	}

	@Override
	public BigDecimal[] gradient(BigDecimal[] point)
	{
		if (derivableFunction!=null){return negate(derivableFunction.gradient(point));}
		else if (twiceDerivableFunction!=null){return negate(twiceDerivableFunction.gradient(point));}
		else throw new CrfException("BUG");
	}

	@Override
	public BigDecimal[][] hessian(BigDecimal[] point)
	{
		if (twiceDerivableFunction!=null)
		{
			BigDecimal[][] ret = new BigDecimal[theSize][theSize];
			BigDecimal[][] originalHessian = twiceDerivableFunction.hessian(point);
			for (int i=0;i<theSize;++i)
			{
				for (int j=0;j<theSize;++j)
				{
					ret[i][j] = originalHessian[i][j].negate();
				}
			}
			return ret;
		}
		else throw new CrfException("BUG");
	}
	
	private NegatedFunction(Function function,
			DerivableFunction derivableFunction,
			TwiceDerivableFunction twiceDerivableFunction)
	{
		super();
		this.function = function;
		this.derivableFunction = derivableFunction;
		this.twiceDerivableFunction = twiceDerivableFunction;
		if (function!=null){this.theSize = function.size();}
		else if (derivableFunction!=null){this.theSize = derivableFunction.size();}
		else if (twiceDerivableFunction!=null){this.theSize = twiceDerivableFunction.size();}
		else throw new CrfException("BUG");
	}
	
	private BigDecimal[] negate(BigDecimal[] array)
	{
		BigDecimal[] ret = new BigDecimal[array.length];
		for (int i=0;i<array.length;++i)
		{
			ret[i] = array[i].negate();
		}
		return ret;
	}
	



	private final Function function;
	private final DerivableFunction derivableFunction;
	private final TwiceDerivableFunction twiceDerivableFunction;
	private final int theSize;
}
