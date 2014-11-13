package org.postagging.utilities;

import java.io.File;
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

	private static class FilenameComparator implements Comparator<File>
	{
		@Override
		public int compare(File o1, File o2)
		{
			return o1.getName().compareTo(o2.getName());
		}
	}
}
