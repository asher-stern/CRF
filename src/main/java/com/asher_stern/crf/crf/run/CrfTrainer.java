package com.asher_stern.crf.crf.run;

import java.util.ArrayList;
import java.util.Iterator;
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
	/**
	 * Change to <tt>true</tt> if you want this debug information.
	 * Note that setting to <tt>true</tt> increases run time.
	 */
	public static final boolean PRINT_DEBUG_INFO_TAG_DIFFERENCE_BETWEEN_ITERATIONS = false;
	
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
		if (is(PRINT_DEBUG_INFO_TAG_DIFFERENCE_BETWEEN_ITERATIONS))
		{
			lbfgsOptimizer.setDebugInfo(new CrfDebugInfo(corpus));
		}
		lbfgsOptimizer.find();
		double[] parameters = lbfgsOptimizer.getPoint();
		if (parameters.length!=features.getFilteredFeatures().length) {throw new CrfException("Number of parameters, returned by LBFGS optimizer, differs from number of features.");}
		
		ArrayList<Double> parametersAsList = arrayDoubleToList(parameters);
		
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
	
	
	
	private static ArrayList<Double> arrayDoubleToList(double[] array)
	{
		ArrayList<Double> ret = new ArrayList<Double>(array.length);
		for (double d : array)
		{
			ret.add(d);
		}
		return ret;
	}


	/**
	 * Used if PRINT_DEBUG_INFO_TAG_DIFFERENCE_BETWEEN_ITERATIONS is true.
	 * Prints debug information of how many tags are equals and how many are different between iterations.
	 *
	 * <p>
	 * Date: Oct 13, 2016
	 * @author Asher Stern
	 *
	 */
	private class CrfDebugInfo implements LbfgsMinimizer.DebugInfo
	{
		public CrfDebugInfo(List<? extends List<? extends TaggedToken<K, G>>> corpus)
		{
			super();
			this.corpus = corpus;
		}

		@Override
		public String info(double[] point)
		{
			ArrayList<Double> parametersAsList = arrayDoubleToList(point);
			CrfModel<K, G> crfModel = new CrfModel<K, G>(crfTags,features,parametersAsList);
			CrfInferencePerformer<K, G> crfInferencePerformer = new CrfInferencePerformer<K, G>(crfModel);
			
			List<G> tagsAllCorpus = new ArrayList<>();
			for (List<? extends TaggedToken<K, G> > taggedSequence : corpus)
			{
				ArrayList<K> sequence = new ArrayList<K>(taggedSequence.size());
				for (TaggedToken<K, G> taggedToken : taggedSequence)
				{
					sequence.add(taggedToken.getToken());
				}
				List<TaggedToken<K,G>> inferencedTaggedSequence = crfInferencePerformer.tagSequence(sequence);
				for (TaggedToken<K, G> taggedToken : inferencedTaggedSequence)
				{
					tagsAllCorpus.add(taggedToken.getTag());
				}
			}
			String ret = null;
			if (tagsPreviousIteration==null)
			{
				ret = "No debug information for first iteration.";
			}
			else
			{
				ret = comparisonInformation(tagsPreviousIteration, tagsAllCorpus);
			}
			tagsPreviousIteration = tagsAllCorpus;
			return ret;
		}
		
		private String comparisonInformation(List<G> previous, List<G> current)
		{
			if (previous.size()!=current.size()) return "Error: not same size";
			Iterator<G> previousIterator = previous.iterator();
			Iterator<G> currentIterator = current.iterator();
			int equal = 0;
			int different = 0;
			while (previousIterator.hasNext())
			{
				G previousTag = previousIterator.next();
				G currentTag = currentIterator.next();
				if (tagsEqual(previousTag, currentTag))
				{
					++equal;
				}
				else
				{
					++different;
				}
			}
			return "Number of equal tags = "+equal+". Number of different tags = "+different+".";
		}
		
		private final List<? extends List<? extends TaggedToken<K, G> >> corpus;
		private List<G> tagsPreviousIteration = null;
	}
	
	private static final <G> boolean tagsEqual(G g1, G g2)
	{
		if (g1==g2) return true;
		if (g1==null) return false;
		if (g2==null) return false;
		return g1.equals(g2);
	}
	
	/**
	 * Returns its argument.<BR>
	 * Used to get rid of "dead code" warnings/errors (in some compilers).
	 */
	private static final boolean is(boolean b)
	{
		return b;
	}
	
	
	private final CrfFeaturesAndFilters<K, G> features;
	private final CrfTags<G> crfTags;
	private final boolean useRegularization;
	private final double sigmaSquare_inverseRegularizationFactor;
	
	private CrfModel<K, G> learnedModel = null;

	private static final Logger logger = Logger.getLogger(CrfTrainer.class);

}
