package com.asher_stern.crf.function.optimization;


/**
 * Indicates that an infinity value has been calculated during function-optimization.
 *
 * <p>
 * Date: Oct 10, 2016
 * @author Asher Stern
 *
 */
@SuppressWarnings("serial")
public class InfinityException extends RuntimeException
{
	public InfinityException(boolean value, boolean vector, boolean nan)
	{
		super(createMessage(value,vector, nan));
		this.value = value;
		this.vector = vector;
		this.nan = nan;
	}
	
	public boolean isValue()
	{
		return value;
	}
	public boolean isVector()
	{
		return vector;
	}
	public boolean isNan()
	{
		return nan;
	}

	private static final String createMessage(boolean value, boolean vector, boolean nan)
	{
		String problem = nan?"NaN":"infinity";
		if (value) return "value is "+problem;
		else if (vector) return "vector is "+problem;
		else return null;
	}

	private final boolean value;
	private final boolean vector;
	private final boolean nan;
}
