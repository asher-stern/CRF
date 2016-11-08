package com.asher_stern.crf.crf;

import static com.asher_stern.crf.crf.CrfUtilities.roughlyEqual;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.asher_stern.crf.utilities.ArbitraryRangeArray;
import com.asher_stern.crf.utilities.CrfException;

import static com.asher_stern.crf.utilities.ArithmeticUtilities.*;

/**
 * Calculates the probability of a sequence of <B>tokens</B>.
 * <BR>
 * While the Viterbi algorithm (see {@link CrfInferenceViterbi}) calculates the probability of a sequence of <B>tags</B> (given
 * a sequence of tokens), the forward-backward algorithm calculates the probability of a sequence of tokens, whatever their
 * real tags are.
 * <BR>
 * Formally, given a sequence of tokens (which is K[] in this class, given as "sentence" to the constructor)
 * \alpha_j(g) is the (unnormalized) probability of the sequence k_0 k_1 ... k_j, where the tag of k_j is "g".<BR>
 * \beta_j(g) is the (unnormalized) probability of the sequence k_{j+1} k_{j+2} ... k_{sentence-length-1} where the tag of
 * k_j (whatever token it is) is "g".
 * <P>
 * Consequently, the probability of the whole sequence (k_0 k_1 ... k_{sentence-length-1}) is \Sum_{g \in G}(\alpha_{sentence-length-1}(g))
 * where G is the set of all tags.
 * Similarly, the probability of the whole sequence (k_0 k_1 ... k_{sentence-length-1}) is also \beta_{-1}(null), where "null" is
 * a "virtual tag" for the "virtual token" that precedes k_0.
 * <BR>
 * Note, therefore, that \Sum_{g \in G}(\alpha_{sentence-length-1}(g)) = \beta_{-1}(null) = <B>The normalization factor Z(x)</B> (where x is the sentence). 
 * <P>
 * Also, the probability for the sequence k_0 k_1 ... k_{sentence-length-1} where the tag for k_j is g and the tag for
 * k_{j-1} is g' is:
 * \alpha_{j-1}(g')*\Psi(j,g,g')*\beta_{j}(g)
 * where \Psi(j,g,g') is the formula for token number j where its tag is g and the tag of token (j-1) is g', i.e.,
 * e^{\Sum{i=0}^{number-of-features}(\theta_i*f_i(j,g,g'))}
 * where \theta_i is parameter number i (these are the parameters learned in the training) and f_i is feature number i (the
 * features are defined by the user).
 * <BR>
 * This formula, \alpha_{j-1}(g')*\Psi(j,g,g')*\beta_{j}(g), is used to calculate the <B>expected</B> feature-values under the
 * model, and is utilized by {@link CrfFeatureValueExpectationByModel}.
 * <P>
 * The forward-backward algorithm has two usages:
 * 1. to calculate the normalization factor Z(x).
 * 2. to calculate the expected feature-values over the corpus.
 * <BR>
 * The first usage is needed to calculate the value of the log likelihood function.
 * The first and the second usages are needed to calculate the gradient of the log likelihood function.
 * <BR>
 * 
 *  @see CrfLogLikelihoodFunction
 *  @see CrfFeatureValueExpectationByModel
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K> type of tokens
 * @param <G> type of tags
 */
public class CrfForwardBackward<K,G>
{
	public CrfForwardBackward(CrfModel<K, G> model, K[] sentence, CrfRememberActiveFeatures<K, G> activeFeaturesForSentence)
	{
		super();
		this.model = model;
		this.sentence = sentence;
		this.activeFeaturesForSentence = activeFeaturesForSentence;
	}
	
	public void setAllTokensFormulaValues(CrfPsi_FormulaAllTokens<K,G> allTokensFormula)
	{
		this.allTokensFormula = allTokensFormula;
	}
	
	public void calculateForwardAndBackward()
	{
		if (null==allTokensFormula)
		{
			allTokensFormula = CrfPsi_FormulaAllTokens.createAndCalculate(model, sentence, activeFeaturesForSentence);
		}
		
		calculateAlphaForward();
		calculateBetaBackward();
		
		if (!roughlyEqual(finalAlpha, finalBeta))
		{
			String errorMessage = "The calculated final-alpha and final-beta, both correspond to Z(x) (the normalization factor) differ.\n"
					+ "Z(x) by alpha (forward) = "+String.format("%-3.3f", finalAlpha)+". Z(x) by beta (backward) = "+String.format("%-3.3f", finalBeta);
			throw new CrfException(errorMessage);
		}
		calculated=true;
	}
	
	public void calculateOnlyNormalizationFactor()
	{
		if (null==allTokensFormula)
		{
			allTokensFormula = CrfPsi_FormulaAllTokens.createAndCalculate(model, sentence, activeFeaturesForSentence);
		}

		calculateAlphaForward();
		onlyNormalizationFactorCalculated = true;
	}
	
	
	
	public Map<G, BigDecimal>[] getAlpha_forward()
	{
		if (!calculated) {throw new CrfException("forward-backward not calculated");}
		return alpha_forward;
	}

	public ArbitraryRangeArray<LinkedHashMap<G, BigDecimal>> getBeta_backward()
	{
		if (!calculated) {throw new CrfException("forward-backward not calculated");}
		return beta_backward;
	}
	
	public BigDecimal getCalculatedNormalizationFactor()
	{
		if ( (!calculated) && (!onlyNormalizationFactorCalculated) ) {throw new CrfException("forward-backward not calculated");}
		return finalAlpha;
	}

	
	
	@SuppressWarnings("unchecked")
	private void calculateAlphaForward()
	{
		alpha_forward = (LinkedHashMap<G, BigDecimal>[]) new LinkedHashMap[sentence.length];
		for (int index=0;index<sentence.length;++index)
		{
			Map<G, BigDecimal> alpha_forwardThisToken = new LinkedHashMap<G, BigDecimal>();
			for (G tag : model.getCrfTags().getTags())
			{
				Set<G> previousTags = CrfUtilities.getPreviousTags(sentence, index, tag, model.getCrfTags());
				BigDecimal sumOverPreviousTags = BigDecimal.ZERO;
				for (G previousTag : previousTags)
				{
					//double valueForPreviousTag = CrfUtilities.oneTokenFormula(model,sentence,index,tag,previousTag);
					BigDecimal valueForPreviousTag = allTokensFormula.getOneTokenFormula(index,tag,previousTag);
					if (index>0)
					{
						BigDecimal previousAlphaValue = alpha_forward[index-1].get(previousTag);
						valueForPreviousTag = safeMultiply(valueForPreviousTag, previousAlphaValue);
					}
					sumOverPreviousTags = safeAdd(sumOverPreviousTags, valueForPreviousTag);
				}
				alpha_forwardThisToken.put(tag, sumOverPreviousTags);
			}
			alpha_forward[index] = alpha_forwardThisToken;
		}
		
		
		finalAlpha = BigDecimal.ZERO;
		Map<G,BigDecimal> alphaLast = alpha_forward[sentence.length-1];
		for (G tag : alphaLast.keySet())
		{
			finalAlpha = safeAdd(finalAlpha, alphaLast.get(tag));
		}
	}
	
	
	
	private void calculateBetaBackward()
	{
		beta_backward = new ArbitraryRangeArray<LinkedHashMap<G, BigDecimal>>(sentence.length+1, -1); // i.e. [-1,0,1,2,...,sentence.length-1]
		beta_backward.set(sentence.length-1,new LinkedHashMap<G, BigDecimal>());
		for (G tag : model.getCrfTags().getTags())
		{
			beta_backward.get(sentence.length-1).put(tag, BigDecimal.ONE);
		}
		
		for (int index=sentence.length-2;index>=(-1);--index)
		{
			LinkedHashMap<G, BigDecimal> betaCurrentToken = new LinkedHashMap<G, BigDecimal>();
			
			Set<G> currentTokenPossibleTags = null;
			if (index<0) {currentTokenPossibleTags=Collections.singleton(null);}
			else {currentTokenPossibleTags = model.getCrfTags().getTags();}
			for (G tag : currentTokenPossibleTags)
			{
				BigDecimal sum = BigDecimal.ZERO;
				for (G nextTag : model.getCrfTags().getCanFollow().get(tag))
				{
					//double valueCurrentTokenCrfFormula = CrfUtilities.oneTokenFormula(model,sentence,index+1,nextTag,tag);
					BigDecimal valueCurrentTokenCrfFormula = allTokensFormula.getOneTokenFormula(index+1,nextTag,tag);
					BigDecimal valueForNextTag = safeMultiply(valueCurrentTokenCrfFormula, beta_backward.get(index+1).get(nextTag));
					sum = safeAdd(sum, valueForNextTag);
				}
				betaCurrentToken.put(tag,sum);
			}
			beta_backward.set(index, betaCurrentToken);
		}
		
		finalBeta = beta_backward.get(-1).get(null);
	}
	
	
	
	
	protected final CrfModel<K, G> model;
	protected final K[] sentence;
	protected final CrfRememberActiveFeatures<K, G> activeFeaturesForSentence;

	private CrfPsi_FormulaAllTokens<K, G> allTokensFormula = null;
	private Map<G, BigDecimal>[] alpha_forward;
	private ArbitraryRangeArray<LinkedHashMap<G, BigDecimal>> beta_backward;
	private BigDecimal finalAlpha = BigDecimal.ZERO;
	private BigDecimal finalBeta = BigDecimal.ZERO;
	
	private boolean calculated = false;
	private boolean onlyNormalizationFactorCalculated = false;
	
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(CrfForwardBackward.class);
}
