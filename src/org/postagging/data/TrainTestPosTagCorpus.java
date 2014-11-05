package org.postagging.data;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public class TrainTestPosTagCorpus
{
	public TrainTestPosTagCorpus(int trainSize, PosTagCorpus realCorpus)
	{
		this(trainSize,0,realCorpus);
	}

	public TrainTestPosTagCorpus(int trainSize, int testSize, PosTagCorpus realCorpus)
	{
		super();
		this.trainSize = trainSize;
		this.testSize = testSize;
		this.realCorpus = realCorpus;
	}



	public PosTagCorpus createTrainCorpus()
	{
		if (trainSize>0)
		{
			return new PosTagCorpus()
			{
				@Override
				public PosTagCorpusReader createReader()
				{
					return new LimitedSizePosTagCorpusReader(realCorpus.createReader(), trainSize);
				}
			};
		}
		else
		{
			return realCorpus;
		}
	}

	
	public PosTagCorpus createTestCorpus()
	{
		if ( (trainSize<=0) && (testSize<=0) ) return realCorpus;
		
		PosTagCorpusReader reader = realCorpus.createReader();
		if (trainSize>0)
		{
			for (int i=0;(i<trainSize)&&(reader.hasNext());++i)
			{
				reader.next();
			}
		}
		
		if (testSize>0)
		{
			reader = new LimitedSizePosTagCorpusReader(reader, testSize);
		}
		
		final PosTagCorpusReader finalReader = reader;
		
		return new PosTagCorpus()
		{
			@Override
			public PosTagCorpusReader createReader()
			{
				return finalReader;
			}
		};
		
	}

	private final int trainSize;
	private final int testSize;
	private final PosTagCorpus realCorpus;
	
}
