package org.postagging.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.postagging.utilities.RuntimeUtilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 */
public class InMemoryPosTagCorpusImplementation implements InMemoryPosTagCorpus
{
	
	public InMemoryPosTagCorpusImplementation(PosTagCorpus fromCorpus)
	{
		init(fromCorpus);
	}

	@Override
	public PosTagCorpusReader createReader()
	{
		final Iterator<List<TaggedToken>> iterator = listSentences.iterator();
		return new PosTagCorpusReader()
		{
			@Override
			public List<TaggedToken> next()
			{
				return iterator.next();
			}
			
			@Override
			public boolean hasNext()
			{
				return iterator.hasNext();
			}
		};
	}
	
	private void init(PosTagCorpus fromCorpus)
	{
		logger.debug("Reading corpus to memory...");
		listSentences = new LinkedList<List<TaggedToken>>();
		PosTagCorpusReader reader = fromCorpus.createReader();
		while (reader.hasNext())
		{
			List<TaggedToken> sentence = reader.next();
			listSentences.add(sentence);
		}
		logger.debug("Reading corpus to memory - done.");
		if (logger.isDebugEnabled())
		{
			logger.debug(RuntimeUtilities.getUsedMemory());
		}
	}
	
	private List<List<TaggedToken>> listSentences;
	
	private static final Logger logger = Logger.getLogger(InMemoryPosTagCorpusImplementation.class);
}
