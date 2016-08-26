package com.asher_stern.crf.postagging.data;

import java.util.Iterator;
import java.util.List;

import com.asher_stern.crf.utilities.TaggedToken;

/**
 * Splits a given corpus into a train corpus and a test corpus.
 * <P>
 * If train-size is provided, and is larger than zero, then the first "train-size" sentences are returned as the train corpus.
 * Otherwise, the whole corpus is provided as the train corpus.
 * <P>
 * If the "train-size" is provided, then the test corpus contains <B>only</B> sentences that are not included in the train corpus.
 * If "test-size" is provided, then only the first "test-size" sentences are included in the test corpus, where "the first sentences"
 * means those which immediately the last sentence in the training (if "train-size" > 0), or the first sentences in the original
 * corpus (if "train-size<=0).
 *  
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public class TrainTestPosTagCorpus<K,G>
{
	public TrainTestPosTagCorpus(int trainSize, Iterable<List<TaggedToken<K, G>>> realCorpus)
	{
		this(trainSize,0,realCorpus);
	}

	public TrainTestPosTagCorpus(int trainSize, int testSize, Iterable<List<TaggedToken<K, G>>> realCorpus)
	{
		super();
		this.trainSize = trainSize;
		this.testSize = testSize;
		this.realCorpus = realCorpus;
	}



	public Iterable<List<TaggedToken<K, G>>> createTrainCorpus()
	{
		if (trainSize>0)
		{
			return new Iterable<List<TaggedToken<K, G>>>()
			{
				@Override
				public Iterator<List<TaggedToken<K, G>>> iterator()
				{
					return new LimitedSizePosTagCorpusReader<K,G>(realCorpus.iterator(), trainSize);
				}
			};
		}
		else
		{
			return realCorpus;
		}
	}

	
	public Iterable<List<TaggedToken<K, G>>> createTestCorpus()
	{
		if ( (trainSize<=0) && (testSize<=0) ) return realCorpus;
		
		Iterator<List<TaggedToken<K, G>>> reader = realCorpus.iterator();
		if (trainSize>0)
		{
			for (int i=0;(i<trainSize)&&(reader.hasNext());++i)
			{
				reader.next();
			}
		}
		
		if (testSize>0)
		{
			reader = new LimitedSizePosTagCorpusReader<K,G>(reader, testSize);
		}
		
		final Iterator<List<TaggedToken<K, G>>> finalReader = reader;
		
		return new Iterable<List<TaggedToken<K, G>>>()
		{
			@Override
			public Iterator<List<TaggedToken<K, G>>> iterator()
			{
				return finalReader;
			}
		};
		
	}

	private final int trainSize;
	private final int testSize;
	private final Iterable<List<TaggedToken<K, G>>> realCorpus;
	
}
