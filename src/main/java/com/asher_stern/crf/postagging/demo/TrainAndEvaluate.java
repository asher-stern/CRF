package com.asher_stern.crf.postagging.demo;

import java.io.File;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.asher_stern.crf.postagging.data.TrainTestPosTagCorpus;
import com.asher_stern.crf.postagging.data.penn.PennCorpus;
import com.asher_stern.crf.postagging.evaluation.AccuracyEvaluator;
import com.asher_stern.crf.postagging.postaggers.PosTagger;
import com.asher_stern.crf.postagging.postaggers.crf.CrfPosTaggerTrainer;
import com.asher_stern.crf.postagging.postaggers.crf.CrfPosTaggerTrainerFactory;
import com.asher_stern.crf.utilities.ExceptionUtil;
import com.asher_stern.crf.utilities.RuntimeUtilities;
import com.asher_stern.crf.utilities.TaggedToken;
import com.asher_stern.crf.utilities.log4j.Log4jInit;

/**
 * An application which trains a {@link PosTagger} by a portion of the given corpus, and then evaluates that {@link PosTagger}
 * over another portion of the given corpus.
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
	 * 4. (optional) directory-name for saving the trained pos-tagger model.
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
			String loadSaveDirectoryName = null;
			if (args.length>=4) {loadSaveDirectoryName = args[3];}
			new TrainAndEvaluate(args[0],Integer.parseInt(args[1]),testSize,loadSaveDirectoryName).go();
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
		}
	}
	

	
	
	
	public TrainAndEvaluate(String corpusDirectory, int trainSize, int testSize, String loadSaveDirectoryName)
	{
		super();
		this.corpusDirectory = corpusDirectory;
		this.trainSize = trainSize;
		this.testSize = testSize;
		this.loadSaveDirectoryName = loadSaveDirectoryName;
		
		logger.info("trainSize = " + trainSize);
		logger.info("testSize = " + testSize);
	}





	public void go()
	{
		TrainTestPosTagCorpus<String,String> corpus = createCorpus();
		logger.info("Training...");
		PosTagger posTagger = train(corpus.createTrainCorpus());
		logger.info("Training - done.");
		logger.info(RuntimeUtilities.getUsedMemory());
		
		logger.info("Evaluating...");
		AccuracyEvaluator evaluator = new AccuracyEvaluator(corpus.createTestCorpus(), posTagger
//				, new PrintWriter(System.out) // comment out this line to prevent tagged test sentence from being printed.
				);
		evaluator.evaluate();
		logger.info(trainingTime);
		logger.info("Accuracy = " + String.format("%-3.3f", evaluator.getAccuracy()));
		logger.info("Correct = "+evaluator.getCorrect());
		logger.info("Incorrect = "+evaluator.getIncorrect());
	}
	

	private TrainTestPosTagCorpus<String,String> createCorpus()
	{
		return new TrainTestPosTagCorpus<String,String>(trainSize, testSize,
				new PennCorpus(new File(corpusDirectory))
				);
	}

	
	private PosTagger train(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus)
	{
		long timeInit = new Date().getTime();
		
//		MajorityPosTaggerTrainer trainer = new MajorityPosTaggerTrainer();
//		trainer.train(inMemoryCorpus);
		
		List<List<? extends TaggedToken<String, String>>> corpusAsList = new LinkedList<List<? extends TaggedToken<String,String>>>();
		for (List<? extends TaggedToken<String, String>> sentence : corpus)
		{
			corpusAsList.add(sentence);
		}
		
		CrfPosTaggerTrainer trainer = new CrfPosTaggerTrainerFactory().createTrainer(corpusAsList);
		trainer.train(corpusAsList);

		long seconds = (new Date().getTime()-timeInit)/1000;
		trainingTime = "Training time (HH:MM:SS) = "+String.format("%02d:%02d:%02d",(seconds/60)/60,(seconds/60)%60,seconds%60);
		logger.info(trainingTime);
		
		if (loadSaveDirectoryName!=null)
		{
			File saveDirectory = new File(loadSaveDirectoryName);
			logger.info("Saving pos tagger into directory: "+saveDirectory.getAbsolutePath()+" ...");
			trainer.save(saveDirectory);
			logger.info("Save done.");
		}
		
		PosTagger posTagger = trainer.getTrainedPosTagger();
		return posTagger;
	}
	

	private final String corpusDirectory;
	private final int trainSize;
	private final int testSize;
	private final String loadSaveDirectoryName;
	
	private String trainingTime = null;
	
	private static final Logger logger = Logger.getLogger(TrainAndEvaluate.class);
}
