package org.postagging.utilities.log4j;

import java.util.Enumeration;

import org.apache.log4j.Appender;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Initializes Log4j: log messages will be printed to screen with prefix of "INFO - ", "DEBUG - ", etc.
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class Log4jInit
{
	
	public static void init()
	{
		init(Level.INFO);
	}
	
	public static void init(Level level)
	{
		if (!alreadyInitialized)
		{
			synchronized(Log4jInit.class)
			{
				if (!alreadyInitialized)
				{
					BasicConfigurator.configure();
					Logger.getRootLogger().setLevel(level);
					
					Enumeration<?> enumAppenders = Logger.getRootLogger().getAllAppenders();
					while (enumAppenders.hasMoreElements())
					{
						Appender appender = (Appender) enumAppenders.nextElement();
						appender.setLayout(new VerySimpleLayout());
					}
					
					alreadyInitialized = true;
				}
			}
		}

	}
	
	private static boolean alreadyInitialized = false;
}
