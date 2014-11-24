package org.crf.utilities;

/**
 * Exception for the pos tagging project.
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class CrfException extends RuntimeException
{
	private static final long serialVersionUID = 5414394233000815286L;

	public CrfException()
	{
		super();
	}

	public CrfException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace)
	{
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public CrfException(String message, Throwable cause)
	{
		super(message, cause);
	}

	public CrfException(String message)
	{
		super(message);
	}

	public CrfException(Throwable cause)
	{
		super(cause);
	}
	

}
