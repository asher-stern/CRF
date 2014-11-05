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
		super();
		this.trainSize = trainSize;
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
		if (trainSize>0)
		{
			return new PosTagCorpus()
			{
				@Override
				public PosTagCorpusReader createReader()
				{
					PosTagCorpusReader reader = realCorpus.createReader();
					for (int i=0;(i<trainSize)&&(reader.hasNext());++i)
					{
						reader.next();
					}
					return reader;
				}
			};
		}
		else
		{
			return realCorpus;
		}
	}

	private final int trainSize;
	private final PosTagCorpus realCorpus;
}
