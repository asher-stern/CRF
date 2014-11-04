package org.postagging.evaluation;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.postagging.data.LimitedSizePosTagCorpusReader;
import org.postagging.data.PosTagCorpusReader;
import org.postagging.data.brown.BrownCorpusReader;
import org.postagging.postaggers.PosTagger;
import org.postagging.postaggers.PosTaggerTrainer;
import org.postagging.postaggers.majority.MajorityPosTaggerTrainer;
import org.postagging.utilities.ExceptionUtil;
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
	 * @param args 1. corpus. 2. train-size (how many train sentences, where the rest are test sentences).
	 * <BR>
	 * If train-size <=0, then the whole corpus is train, and the text is on the training data.
	 */
	public static void main(String[] args)
	{
		Log4jInit.init(Level.DEBUG);
		try
		{
			new TrainAndEvaluate(args[0],Integer.parseInt(args[1])).go();
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
		}
	}
	

	
	
	
	public TrainAndEvaluate(String brownDirectory, int trainSize)
	{
		super();
		this.brownDirectory = brownDirectory;
		this.trainSize = trainSize;
	}





	public void go()
	{
		PosTagCorpusReader corpus = createCorpus();
		PosTagCorpusReader trainCorpus = null;
		if (trainSize>0)
		{
			trainCorpus = new LimitedSizePosTagCorpusReader(corpus, trainSize);
		}
		else
		{
			trainCorpus = corpus;
		}
		logger.info("Training...");
		PosTaggerTrainer trainer = createTrainer();
		trainer.train(trainCorpus);
		PosTagger posTagger = trainer.getTrainedPosTagger();
		logger.info("Training - done.");
		
		logger.info("Evaluating...");
		if (trainSize<=0)
		{
			corpus = createCorpus();
		}
		AccuracyEvaluator evaluator = new AccuracyEvaluator(corpus, posTagger);
		evaluator.evaluate();
		logger.info("Accuracy = " + String.format("%-3.3f", evaluator.getAccuracy()));
		logger.info("Correct = "+evaluator.getCorrect());
		logger.info("Incorrect = "+evaluator.getIncorrect());
	}
	

	private PosTagCorpusReader createCorpus()
	{
		return new BrownCorpusReader(brownDirectory);
	}

	private PosTaggerTrainer createTrainer()
	{
		return new MajorityPosTaggerTrainer();
	}
	

	private final String brownDirectory;
	private final int trainSize;
	
	private static final Logger logger = Logger.getLogger(TrainAndEvaluate.class);
}
