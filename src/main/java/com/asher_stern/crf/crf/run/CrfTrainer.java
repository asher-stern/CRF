package com.asher_stern.crf.crf.run;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.asher_stern.crf.crf.CrfLogLikelihoodFunction;
import com.asher_stern.crf.crf.CrfModel;
import com.asher_stern.crf.crf.CrfTags;
import com.asher_stern.crf.crf.filters.CrfFeaturesAndFilters;
import com.asher_stern.crf.function.DerivableFunction;
import com.asher_stern.crf.function.optimization.LbfgsMinimizer;
import com.asher_stern.crf.function.optimization.NegatedFunction;
import com.asher_stern.crf.utilities.CrfException;
import com.asher_stern.crf.utilities.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 23, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfTrainer<K,G>
{
	public static final double DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR = 10.0;
	public static final boolean DEFAULT_USE_REGULARIZATION = true;

	
	public CrfTrainer(CrfFeaturesAndFilters<K, G> features, CrfTags<G> crfTags)
	{
		this(features,crfTags,DEFAULT_USE_REGULARIZATION,DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR);
	}

	public CrfTrainer(CrfFeaturesAndFilters<K, G> features, CrfTags<G> crfTags,
			boolean useRegularization, double sigmaSquare_inverseRegularizationFactor)
	{
		super();
		this.features = features;
		this.crfTags = crfTags;
		this.useRegularization = useRegularization;
		this.sigmaSquare_inverseRegularizationFactor = sigmaSquare_inverseRegularizationFactor;
	}


	public void train(List<? extends List<? extends TaggedToken<K, G> >> corpus)
	{
		logger.info("CRF training: Number of tags = "+crfTags.getTags().size()+". Number of features = "+features.getFilteredFeatures().length +".");
		logger.info("Creating log likelihood function.");
		DerivableFunction convexNegatedCrfFunction = NegatedFunction.fromDerivableFunction(createLogLikelihoodFunctionConcave(corpus));
		logger.info("Optimizing log likelihood function.");
		LbfgsMinimizer lbfgsOptimizer = new LbfgsMinimizer(convexNegatedCrfFunction);
		lbfgsOptimizer.find();
		double[] parameters = lbfgsOptimizer.getPoint();
		if (parameters.length!=features.getFilteredFeatures().length) {throw new CrfException("Number of parameters, returned by LBFGS optimizer, differs from number of features.");}
		
		ArrayList<Double> parametersAsList = new ArrayList<Double>(parameters.length);
		for (double parameter : parameters)
		{
			parametersAsList.add(parameter);
		}
		
		learnedModel = new CrfModel<K, G>(crfTags,features,parametersAsList);
		logger.info("Training of CRF - done.");
	}
	
	
	
	
	
	
	public CrfModel<K, G> getLearnedModel()
	{
		return learnedModel;
	}

	public CrfInferencePerformer<K, G> getInferencePerformer()
	{
		if (null==learnedModel) throw new CrfException("Not yet trained");
		return new CrfInferencePerformer<K,G>(learnedModel);
		
	}

	
	
	private DerivableFunction createLogLikelihoodFunctionConcave(List<? extends List<? extends TaggedToken<K, G> >> corpus)
	{
		return new CrfLogLikelihoodFunction<K, G>(corpus,crfTags,features,useRegularization,sigmaSquare_inverseRegularizationFactor);
	}


	private final CrfFeaturesAndFilters<K, G> features;
	private final CrfTags<G> crfTags;
	private final boolean useRegularization;
	private final double sigmaSquare_inverseRegularizationFactor;
	
	private CrfModel<K, G> learnedModel = null;

	private static final Logger logger = Logger.getLogger(CrfTrainer.class);

}
