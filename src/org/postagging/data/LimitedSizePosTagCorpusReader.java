package org.postagging.data;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class LimitedSizePosTagCorpusReader implements PosTagCorpusReader
{
	public LimitedSizePosTagCorpusReader(PosTagCorpusReader realCorpus, int size)
	{
		super();
		this.realCorpus = realCorpus;
		this.size = size;
	}

	@Override
	public boolean hasNext()
	{
		return (realCorpus.hasNext()&&(index<size));
	}

	@Override
	public List<TaggedToken> next()
	{
		if (index<size)
		{
			++index;
			return realCorpus.next();
		}
		else throw new NoSuchElementException();
	}

	
	private final PosTagCorpusReader realCorpus;
	private final int size;
	
	private int index=0;
}
