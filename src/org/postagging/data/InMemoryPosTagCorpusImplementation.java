package org.postagging.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.postagging.utilities.TaggedToken;
import org.postagging.utilities.RuntimeUtilities;

/**
 * Loads a corpus into the internal memory.
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 */
public class InMemoryPosTagCorpusImplementation<K,G> implements InMemoryPosTagCorpus<K,G>
{
	
	public InMemoryPosTagCorpusImplementation(PosTagCorpus<K,G> fromCorpus)
	{
		init(fromCorpus);
	}

	@Override
	public PosTagCorpusReader<K,G> iterator()
	{
		final Iterator<List<? extends TaggedToken<K, G>>> iterator = listSentences.iterator();
		return new PosTagCorpusReader<K,G>()
		{
			@Override
			public List<? extends TaggedToken<K, G>> next()
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
	
	private void init(PosTagCorpus<K,G> fromCorpus)
	{
		logger.debug("Reading corpus to memory...");
		listSentences = new LinkedList<List<? extends TaggedToken<K, G>>>();
		PosTagCorpusReader<K,G> reader = fromCorpus.iterator();
		while (reader.hasNext())
		{
			List<? extends TaggedToken<K, G>> sentence = reader.next();
			listSentences.add(sentence);
		}
		if (logger.isDebugEnabled())
		{
			logger.debug("Reading corpus to memory - done.");
			logger.debug("Number of sentences = " + listSentences.size());
		}
		
		if (logger.isDebugEnabled())
		{
			logger.debug(RuntimeUtilities.getUsedMemory());
		}
	}
	
	private List<List<? extends TaggedToken<K, G>>> listSentences;
	
	private static final Logger logger = Logger.getLogger(InMemoryPosTagCorpusImplementation.class);
}
