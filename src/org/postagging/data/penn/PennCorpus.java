package org.postagging.data.penn;

import java.io.File;
import java.util.List;

import org.postagging.utilities.TaggedToken;

/**
 * Size of corpus = 49208 sentences
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 */
public class PennCorpus implements Iterable<List<TaggedToken<String, String>>>
{
	public PennCorpus(File directory)
	{
		super();
		this.directory = directory;
	}

	@Override
	public PennCorpusReader iterator()
	{
		return new PennCorpusReader(directory);
	}
	
	private final File directory;
}
