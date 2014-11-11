package org.postagging.postaggers.crf;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;

import org.apache.log4j.Logger;
import org.postagging.crf.CrfLogLikelihoodFunction;
import org.postagging.crf.CrfModel;
import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.function.DerivableFunction;
import org.postagging.function.optimization.LbfgsMinimizer;
import org.postagging.function.optimization.NegatedFunction;
import org.postagging.postaggers.PosTagger;
import org.postagging.postaggers.PosTaggerTrainer;
import org.postagging.utilities.PosTaggerException;


/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class CrfPosTaggerTrainer implements PosTaggerTrainer<InMemoryPosTagCorpus<String,String>>
{
	public static final double DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR = 10.0;
	public static final boolean DEFAULT_USE_REGULARIZATION = true;
	
	
	public CrfPosTaggerTrainer(CrfFeaturesAndFilters<String, String> features, Set<String> tags)
	{
		this(features,tags,DEFAULT_USE_REGULARIZATION,DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR);
	}

	public CrfPosTaggerTrainer(CrfFeaturesAndFilters<String, String> features, Set<String> tags,
			boolean useRegularization, double sigmaSquare_inverseRegularizationFactor)
	{
		super();
		this.features = features;
		this.tags = tags;
		this.useRegularization = useRegularization;
		this.sigmaSquare_inverseRegularizationFactor = sigmaSquare_inverseRegularizationFactor;
	}


	@Override
	public void train(InMemoryPosTagCorpus<String, String> corpus)
	{
		logger.info("CRF pos tagger training: Number of tags = "+tags.size()+". Number of features = "+features.getFilteredFeatures().length +".");
		logger.info("Creating log likelihood function.");
		DerivableFunction convexNegatedCrfFunction = NegatedFunction.fromDerivableFunction(createLogLikelihoodFunctionConcave(corpus));
		logger.info("Optimizing log likelihood function.");
		LbfgsMinimizer lbfgsOptimizer = new LbfgsMinimizer(convexNegatedCrfFunction);
		lbfgsOptimizer.find();
		double[] parameters = lbfgsOptimizer.getPoint();
		if (parameters.length!=features.getFilteredFeatures().length) {throw new PosTaggerException("Number of parameters, returned by LBFGS optimizer, differs from number of features.");}
		
		ArrayList<Double> parametersAsList = new ArrayList<Double>(parameters.length);
		for (double parameter : parameters)
		{
			parametersAsList.add(parameter);
		}
		
		learnedModel = new CrfModel<String, String>(tags,features,parametersAsList);
		posTagger = new CrfPosTagger(learnedModel);
		logger.info("Training of CRF pos tagger - done.");
	}

	
	@Override
	public PosTagger getTrainedPosTagger()
	{
		if (null==posTagger) {throw new PosTaggerException("Not trained.");}
		return posTagger;
	}

	@Override
	public void save(File modelDirectory)
	{
		throw new PosTaggerException("Not yet implemented.");
	}
	
	
	
	private DerivableFunction createLogLikelihoodFunctionConcave(InMemoryPosTagCorpus<String, String> corpus)
	{
		return new CrfLogLikelihoodFunction<String, String>(corpus,tags,features,useRegularization,sigmaSquare_inverseRegularizationFactor);
	}
	

	
	private final CrfFeaturesAndFilters<String, String> features;
	private final Set<String> tags;
	private final boolean useRegularization;
	private final double sigmaSquare_inverseRegularizationFactor;
	
	private CrfModel<String, String> learnedModel = null;
	private CrfPosTagger posTagger = null;
	
	private static final Logger logger = Logger.getLogger(CrfPosTaggerTrainer.class);
}
