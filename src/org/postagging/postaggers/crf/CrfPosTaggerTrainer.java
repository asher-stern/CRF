package org.postagging.postaggers.crf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.postagging.crf.CrfLogLikelihoodFunction;
import org.postagging.crf.CrfModel;
import org.postagging.crf.CrfTags;
import org.postagging.crf.features.CrfFeaturesAndFilters;
import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.function.DerivableFunction;
import org.postagging.function.optimization.LbfgsMinimizer;
import org.postagging.function.optimization.NegatedFunction;
import org.postagging.postaggers.PosTaggerTrainer;
import org.postagging.utilities.AbsoluteValueComparator;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.PosTaggerUtilities;


/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class CrfPosTaggerTrainer implements PosTaggerTrainer<InMemoryPosTagCorpus<String,String>>
{
	public static final String SAVE_LOAD_FILE_NAME = "crfptmdl.ser";
	public static final String HUMAN_READABLE_FILE_NAME = "rdbl_mdl.txt";
	
	public static final double DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR = 10.0;
	public static final boolean DEFAULT_USE_REGULARIZATION = true;
	
	
	public CrfPosTaggerTrainer(CrfFeaturesAndFilters<String, String> features, CrfTags<String> crfTags)
	{
		this(features,crfTags,DEFAULT_USE_REGULARIZATION,DEFAULT_SIGMA_SQUARED_INVERSE_REGULARIZATION_FACTOR);
	}

	public CrfPosTaggerTrainer(CrfFeaturesAndFilters<String, String> features, CrfTags<String> crfTags,
			boolean useRegularization, double sigmaSquare_inverseRegularizationFactor)
	{
		super();
		this.features = features;
		this.crfTags = crfTags;
		this.useRegularization = useRegularization;
		this.sigmaSquare_inverseRegularizationFactor = sigmaSquare_inverseRegularizationFactor;
	}


	@Override
	public void train(InMemoryPosTagCorpus<String, String> corpus)
	{
		logger.info("CRF pos tagger training: Number of tags = "+crfTags.getTags().size()+". Number of features = "+features.getFilteredFeatures().length +".");
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
		
		learnedModel = new CrfModel<String, String>(crfTags,features,parametersAsList);
		posTagger = new CrfPosTagger(learnedModel);
		logger.info("Training of CRF pos tagger - done.");
	}

	
	@Override
	public CrfPosTagger getTrainedPosTagger()
	{
		if (null==posTagger) {throw new PosTaggerException("Not trained.");}
		return posTagger;
	}

	@Override
	public void save(File modelDirectory)
	{
		if (null==posTagger) {throw new PosTaggerException("Not trained.");}
		
		if (!modelDirectory.exists()) {throw new PosTaggerException("Given directory: "+modelDirectory.getAbsolutePath()+" does not exist.");}
		if (!modelDirectory.isDirectory()) {throw new PosTaggerException("The loader requires a directory, but was provided with a file: "+modelDirectory.getAbsolutePath()+".");}

		
		try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(new File(modelDirectory,SAVE_LOAD_FILE_NAME))))
		{
			outputStream.writeObject(learnedModel);
		}
		catch (IOException e)
		{
			throw new PosTaggerException("Saving the pos tagger failed.",e);
		}
		
		try
		{
			createHumanReadableModelFile(new File(modelDirectory,HUMAN_READABLE_FILE_NAME));
		}
		catch(RuntimeException e)
		{
			logger.error("Could not write a human readable file. However, this does NOT cause the program to stop.",e);
		}
	}
	
	
	
	private DerivableFunction createLogLikelihoodFunctionConcave(InMemoryPosTagCorpus<String, String> corpus)
	{
		return new CrfLogLikelihoodFunction<String, String>(corpus,crfTags,features,useRegularization,sigmaSquare_inverseRegularizationFactor);
	}
	
	
	private void createHumanReadableModelFile(File file)
	{
		try(PrintWriter writer = new PrintWriter(file))
		{
			writer.println("This is a human readable model file, provided for convenience only. It is NOT used by the system at all. Changing, and even deleting this file has no effect on the system, and the loaded pos-tagger.");
			
			CrfFilteredFeature<String, String>[] features = learnedModel.getFeatures().getFilteredFeatures();
			ArrayList<Double> parameters = learnedModel.getParameters();
			if (features.length!=parameters.size())
			{
				throw new PosTaggerException("features.length!=parameters.size()");
			}

			Map<Integer,Double> parametersMap = PosTaggerUtilities.listToMap(parameters);
			List<Integer> sortedIndexes = PosTaggerUtilities.sortByValue(parametersMap, Collections.reverseOrder(new AbsoluteValueComparator()));
			
			for (int index : sortedIndexes)
			{
				double parameter = parametersMap.get(index);
				writer.printf("%-10.5f\t\t%s\n",parameter,features[index].getFeature().toString());
			}
		}
		catch (FileNotFoundException e)
		{
			throw new PosTaggerException("Could not write human-readable model file.",e);
		}
	}
	
	

	

	
	private final CrfFeaturesAndFilters<String, String> features;
	private final CrfTags<String> crfTags;
	private final boolean useRegularization;
	private final double sigmaSquare_inverseRegularizationFactor;
	
	private CrfModel<String, String> learnedModel = null;
	private CrfPosTagger posTagger = null;
	
	private static final Logger logger = Logger.getLogger(CrfPosTaggerTrainer.class);
}
