package org.postagging.utilities;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A collection of static functions for handling files.
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class FileUtilities
{
	/**
	 * Returns an array of File, sorted by their name (alphabetically).
	 * @param files
	 * @return
	 */
	public static File[] getSortedByName(File[] files)
	{
		ArrayList<File> list = new ArrayList<File>(files.length);
		for (File file : files) {list.add(file);}
		Collections.sort(list,new FilenameComparator());
		
		File[] ret = new File[list.size()];
		int index=0;
		for (File file : list)
		{
			ret[index] = file;
			++index;
		}
		return ret;
	}
	
	public static String readTextFile(File file)
	{
		StringBuilder sb = new StringBuilder();
		try(BufferedReader reader = new BufferedReader(new FileReader(file)))
		{
			String line = reader.readLine();
			while (line != null)
			{
				sb.append(line).append("\n");
				line = reader.readLine();
			}
		}
		catch (IOException e)
		{
			throw new PosTaggerException("IO problem.",e);
		}
		
		return sb.toString();
	}

	private static class FilenameComparator implements Comparator<File>
	{
		@Override
		public int compare(File o1, File o2)
		{
			return o1.getName().compareTo(o2.getName());
		}
	}
	
	
	
}
