package org.postagging.data.penn;

import java.io.File;

import org.postagging.data.PosTagCorpus;

/**
 * Size of corpus = 1173766
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 */
public class PennCorpus implements PosTagCorpus<String,String>
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
