package org.postagging.lingpipe;

import java.io.File;
import java.io.IOException;

import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.postaggers.PosTagger;
import org.postagging.postaggers.PosTaggerTrainer;
import org.postagging.utilities.PosTaggerException;

import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.crf.ChainCrf;
import com.aliasi.crf.ChainCrfFeatureExtractor;
import com.aliasi.io.LogLevel;
import com.aliasi.io.Reporter;
import com.aliasi.io.Reporters;
import com.aliasi.stats.AnnealingSchedule;
import com.aliasi.stats.RegressionPrior;
import com.aliasi.tag.Tagging;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public class LingPipeWrapperPosTaggerTrainer implements PosTaggerTrainer<InMemoryPosTagCorpus>
{

	@Override
	public void train(InMemoryPosTagCorpus corpus)
	{
		LingPipeCorpusCreator corpusCreator = new LingPipeCorpusCreator();
		Corpus<ObjectHandler<Tagging<String>>> lingPipeCorpus = corpusCreator.createTrainCorpus(corpus);
        ChainCrfFeatureExtractor<String> featureExtractor = new LingPipeSimpleChainCrfFeatureExtractor();
        
        boolean addIntercept = true;
        int minFeatureCount = 1;
        boolean cacheFeatures = false; // true - faster, but uses more space.
        boolean allowUnseenTransitions = true;
        double priorVariance = 4.0;
        boolean uninformativeIntercept = true;
        RegressionPrior prior = RegressionPrior.gaussian(priorVariance, uninformativeIntercept);
        //int priorBlockSize = 3; // update every X instances. Perhaps high value can be faster.
        int priorBlockSize = 20;
        
        //double initialLearningRate = 0.05;
        double initialLearningRate = 0.8;
        //double learningRateDecay = 0.995;
        //double learningRateDecay = 0.9;
        //AnnealingSchedule annealingSchedule = AnnealingSchedule.exponential(initialLearningRate, learningRateDecay);
        AnnealingSchedule annealingSchedule = AnnealingSchedule.inverse(initialLearningRate, 20.0);
        //AnnealingSchedule annealingSchedule = AnnealingSchedule.constant(initialLearningRate);
        
        // double minImprovement = 0.00001; // if improvement is less than X, then stop (but run at list minEpochs epochs). I guess high value might save time.
        double minImprovement = 0.001;
        int minEpochs = 2;
        int maxEpochs = 2000;
        
        Reporter reporter = Reporters.stdOut().setLevel(LogLevel.DEBUG);
        
        try
        {
        	lingPipeCrf = ChainCrf.estimate(lingPipeCorpus,
        			featureExtractor,
        			addIntercept,
        			minFeatureCount,
        			cacheFeatures,
        			allowUnseenTransitions,
        			prior,
        			priorBlockSize,
        			annealingSchedule,
        			minImprovement,
        			minEpochs,
        			maxEpochs,
        			reporter);
        	
        	
        }
        catch (IOException e)
        {
        	throw new PosTaggerException("Ling pipe training failed.",e);
        }
	}

	@Override
	public PosTagger getTrainedPosTagger()
	{
		return new LingPipeWrapperPosTagger(lingPipeCrf);
	}

	@Override
	public void save(File modelDirectory)
	{
		throw new PosTaggerException("Not yet implemented.");
	}

	private ChainCrf<String> lingPipeCrf = null;
}
