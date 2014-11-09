package org.postagging.crf;


/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K>
 * @param <G>
 */
public abstract class CrfInference<K,G>
{
	public CrfInference(CrfModel<K, G> model, K[] sentence)
	{
		this.model = model;
		this.sentence = sentence;
		
	}
	
	public abstract G[] inferBestTagSequence();
	
	
	
	protected final CrfModel<K, G> model;
	protected final K[] sentence;
}
