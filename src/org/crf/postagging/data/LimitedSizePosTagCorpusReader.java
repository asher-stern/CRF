package org.crf.postagging.data;

import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.crf.utilities.TaggedToken;

/**
 * A corpus that contains only a portion of an original corpus.
 * It contains only the fixed number of the first sentences that are included in the original corpus.
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class LimitedSizePosTagCorpusReader<K,G> implements Iterator<List<TaggedToken<K, G>>>
{
	public LimitedSizePosTagCorpusReader(Iterator<List<TaggedToken<K, G>>> realCorpus, int size)
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
	public List<TaggedToken<K, G> > next()
	{
		if (index<size)
		{
			++index;
			return realCorpus.next();
		}
		else throw new NoSuchElementException();
	}

	
	private final Iterator<List<TaggedToken<K, G>>> realCorpus;
	private final int size;
	
	private int index=0;
}
