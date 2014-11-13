package org.postagging.utilities;

/**
 * A collection of static functions for the JVM runtime.
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 */
public class RuntimeUtilities
{
	public static final long MEGA = 1048576;
	
	public static String getUsedMemory()
	{
		final Runtime runtime = Runtime.getRuntime();
		return "Used memory: "+String.valueOf((runtime.totalMemory()-runtime.freeMemory())/MEGA)+" MB";
	}

}
