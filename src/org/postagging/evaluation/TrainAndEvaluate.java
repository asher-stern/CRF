package org.postagging.evaluation;

import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.data.InMemoryPosTagCorpusImplementation;
import org.postagging.data.PosTagCorpus;
import org.postagging.data.PosTagCorpusReader;
import org.postagging.data.TrainTestPosTagCorpus;
import org.postagging.data.brown.BrownCorpusReader;
import org.postagging.postaggers.PosTagger;
import org.postagging.postaggers.crf.CrfPosTaggerTrainer;
import org.postagging.postaggers.crf.CrfPosTaggerTrainerFactory;
import org.postagging.utilities.ExceptionUtil;
import org.postagging.utilities.RuntimeUtilities;
import org.postagging.utilities.log4j.Log4jInit;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class TrainAndEvaluate
{

	/**
	 * 
	 * @param args 1. corpus. 2. train-size (how many train sentences, where the rest are test sentences). 3. (optional) test-size
	 * <BR>
	 * If train-size <=0, then the whole corpus is train, and the test is on the training data.
	 * <BR>
	 * If test-size is omitted or <=0, then the whole (remaining sentences in the) corpus is the test data.
	 */
	public static void main(String[] args)
	{
		Log4jInit.init(Level.DEBUG);
		try
		{
			int testSize = 0;
			if (args.length>=3) {testSize = Integer.parseInt(args[2]);}
			new TrainAndEvaluate(args[0],Integer.parseInt(args[1]),testSize).go();
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
		}
	}
	

	
	
	
	public TrainAndEvaluate(String brownDirectory, int trainSize, int testSize)
	{
		super();
		this.brownDirectory = brownDirectory;
		this.trainSize = trainSize;
		this.testSize = testSize;
	}





	public void go()
	{
		TrainTestPosTagCorpus<String,String> corpus = createCorpus();
		logger.info("Training...");
		PosTagger posTagger = train(corpus.createTrainCorpus());
		logger.info("Training - done.");
		logger.info(RuntimeUtilities.getUsedMemory());
		
		logger.info("Evaluating...");
		AccuracyEvaluator evaluator = new AccuracyEvaluator(corpus.createTestCorpus(), posTagger);
		evaluator.evaluate();
		logger.info("Accuracy = " + String.format("%-3.3f", evaluator.getAccuracy()));
		logger.info("Correct = "+evaluator.getCorrect());
		logger.info("Incorrect = "+evaluator.getIncorrect());
	}
	

	private TrainTestPosTagCorpus<String,String> createCorpus()
	{
		return new TrainTestPosTagCorpus<String,String>(trainSize, testSize,
				new PosTagCorpus<String,String>()
				{
					@Override
					public PosTagCorpusReader<String,String> iterator()
					{
						return new BrownCorpusReader(brownDirectory);
					}
				}
		);
	}

	
	private PosTagger train(PosTagCorpus<String,String> corpus)
	{
		InMemoryPosTagCorpus<String,String> inMemoryCorpus = new InMemoryPosTagCorpusImplementation<String,String>(corpus);

		long timeInit = new Date().getTime();
		
//		MajorityPosTaggerTrainer trainer = new MajorityPosTaggerTrainer();
//		trainer.train(inMemoryCorpus);
		
		//LingPipeWrapperPosTaggerTrainer trainer = new LingPipeWrapperPosTaggerTrainer();
		
		CrfPosTaggerTrainerFactory factory = new CrfPosTaggerTrainerFactory();
		CrfPosTaggerTrainer trainer = factory.createPosTaggerTrainer(inMemoryCorpus);
		trainer.train(inMemoryCorpus);

		long seconds = (new Date().getTime()-timeInit)/1000;
		logger.info("Training time (HH:MM:SS) = "+String.format("%02d:%02d:%02d",(seconds/60)/60,(seconds/60)%60,seconds%60));
		
		PosTagger posTagger = trainer.getTrainedPosTagger();
		return posTagger;
	}
	

	private final String brownDirectory;
	private final int trainSize;
	private final int testSize;
	
	private static final Logger logger = Logger.getLogger(TrainAndEvaluate.class);
}
