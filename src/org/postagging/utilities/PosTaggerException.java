package org.postagging.utilities;

/**
 * Exception for the pos tagging project.
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class PosTaggerException extends RuntimeException
{
	private static final long serialVersionUID = 5414394233000815286L;

	public PosTaggerException()
	{
		super();
	}

	public PosTaggerException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public PosTaggerException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public PosTaggerException(String message)
	{
		super(message);
	}

	public PosTaggerException(Throwable cause)
	{
		super(cause);
	}
	

}
