package com.asher_stern.crf.postagging.data.penn;


import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.FileUtilities;
import com.asher_stern.crf.utilities.TaggedToken;

/**
 * Iterator for the Penn Tree-Bank. See {@link PennCorpus}.
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 */
public class PennCorpusReader implements Iterator<List<TaggedToken<String, String>>>
{
	public static final String PENN_FILE_SUFFIX = ".mrg";

	public PennCorpusReader(File directory)
	{
		super();
		this.directory = directory;
		initialize();
	}

	@Override
	public boolean hasNext()
	{
		return (fileIterator.hasNext() || treeIterator.hasNext());
	}

	@Override
	public List<TaggedToken<String, String>> next()
	{
		while ((null==treeIterator) || (!treeIterator.hasNext()))
		{
			File file = fileIterator.next(); // might throw NoSuchElementException.
			String fileContents = FileUtilities.readTextFile(file);
			PennFileContentsParser parser = new PennFileContentsParser(fileContents.toCharArray());
			parser.parse();
			treeIterator = parser.getTrees().iterator();
			// if (logger.isDebugEnabled()) {logger.debug(file.getName());}
		}
		PennParserTreeNode tree = treeIterator.next();
		PennTreeToPosTaggedSentence extractor = new PennTreeToPosTaggedSentence(tree);
		extractor.extractPosTaggedSentence();
		return extractor.getSentence();
	}
	
	
	
	private void initialize()
	{
		File[] files = directory.listFiles(new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				return (pathname.isFile() && pathname.getName().endsWith(PENN_FILE_SUFFIX));
			}
		});
		if (files.length<=0)
		{
			throw new CrfException("Wrong directory: no Penn TreeBank file has been detected.");
		}
		
		files = FileUtilities.getSortedByName(files);
		
		ArrayList<File> filesAsList = new ArrayList<File>(files.length);
		for (File file : files)
		{
			filesAsList.add(file);
		}
		
		fileIterator = filesAsList.iterator();
	}

	private final File directory;
	
	private Iterator<File> fileIterator = null;
	private Iterator<PennParserTreeNode> treeIterator = null;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(PennCorpusReader.class);
}
