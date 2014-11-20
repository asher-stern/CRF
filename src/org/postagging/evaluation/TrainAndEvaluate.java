package org.postagging.evaluation;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.data.InMemoryPosTagCorpusImplementation;
import org.postagging.data.PosTagCorpus;
import org.postagging.data.TrainTestPosTagCorpus;
import org.postagging.data.penn.PennCorpus;
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
			String loadSaveDirectoryName = null;
			if (args.length>=4) {loadSaveDirectoryName = args[3];}
			new TrainAndEvaluate(args[0],Integer.parseInt(args[1]),testSize,loadSaveDirectoryName).go();
		}
		catch(Throwable t)
		{
			ExceptionUtil.logException(t, logger);
		}
	}
	

	
	
	
	public TrainAndEvaluate(String brownDirectory, int trainSize, int testSize, String loadSaveDirectoryName)
	{
		super();
		this.corpusDirectory = brownDirectory;
		this.trainSize = trainSize;
		this.testSize = testSize;
		this.loadSaveDirectoryName = loadSaveDirectoryName;
		
		System.out.println("trainSize = " + trainSize);
		System.out.println("testSize = " + testSize);
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

//		return new TrainTestPosTagCorpus<String,String>(trainSize, testSize,
//				new PosTagCorpus<String,String>()
//				{
//					@Override
//					public PosTagCorpusReader<String,String> iterator()
//					{
//						return new BrownCorpusReader(corpusDirectory);
//					}
//				}
//				);
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
