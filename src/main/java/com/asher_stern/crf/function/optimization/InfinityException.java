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
	public InfinityException(boolean value, boolean gradient)
	{
		super(createMessage(value,gradient));
		this.value = value;
		this.gradient = gradient;
	}
	
	public boolean isValue()
	{
		return value;
	}
	public boolean isGradient()
	{
		return gradient;
	}
	
	private static final String createMessage(boolean value, boolean gradient)
	{
		if (value) return "value is infinity";
		else if (gradient) return "gradient is infinity";
		else return null;
	}

	private final boolean value;
	private final boolean gradient;
}
