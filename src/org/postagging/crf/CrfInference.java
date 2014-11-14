package org.postagging.crf;


/**
 * Given a sentence (a test example) and a model, this class finds the most probable sequence of tags for the tokens
 * of this sentence, under the given model.
 * <P>
 * The inference implementation is the Viterbi algorithm, implemented in {@link CrfInferenceViterbi}.
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 * 
 * @see CrfInferenceViterbi
 *
 * @param <K>
 * @param <G>
 */
public abstract class CrfInference<K,G>
{
	/**
	 * Constructs a {@link CrfInference} object for the given sentence under the given model.
	 * @param model
	 * @param sentence
	 */
	public CrfInference(CrfModel<K, G> model, K[] sentence)
	{
		this.model = model;
		this.sentence = sentence;
		
	}
	
	/**
	 * Finds and returns the most probable sequence of tags for the sentence given in the constructor.
	 * @return
	 */
	public abstract G[] inferBestTagSequence();
	
	
	
	protected final CrfModel<K, G> model;
	protected final K[] sentence;
}
