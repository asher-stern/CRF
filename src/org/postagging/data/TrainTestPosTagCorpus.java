package org.postagging.data;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public class TrainTestPosTagCorpus<K,G>
{
	public TrainTestPosTagCorpus(int trainSize, PosTagCorpus<K,G> realCorpus)
	{
		this(trainSize,0,realCorpus);
	}

	public TrainTestPosTagCorpus(int trainSize, int testSize, PosTagCorpus<K,G> realCorpus)
	{
		super();
		this.trainSize = trainSize;
		this.testSize = testSize;
		this.realCorpus = realCorpus;
	}



	public PosTagCorpus<K,G> createTrainCorpus()
	{
		if (trainSize>0)
		{
			return new PosTagCorpus<K,G>()
			{
				@Override
				public PosTagCorpusReader<K,G> iterator()
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

	
	public PosTagCorpus<K,G> createTestCorpus()
	{
		if ( (trainSize<=0) && (testSize<=0) ) return realCorpus;
		
		PosTagCorpusReader<K,G> reader = realCorpus.iterator();
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
		
		final PosTagCorpusReader<K,G> finalReader = reader;
		
		return new PosTagCorpus<K,G>()
		{
			@Override
			public PosTagCorpusReader<K,G> iterator()
			{
				return finalReader;
			}
		};
		
	}

	private final int trainSize;
	private final int testSize;
	private final PosTagCorpus<K,G> realCorpus;
	
}
