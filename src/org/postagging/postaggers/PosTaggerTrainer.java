package org.postagging.postaggers;

import java.io.File;

import org.postagging.data.PosTagCorpus;

/**
 * Trains a {@link PosTagger} with the given corpus, and provides method to get the trained pos-tagger and
 * saving its model in the file-system.
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public interface PosTaggerTrainer<C extends PosTagCorpus<String,String>>
{
	/**
	 * Train the pos-tagger with the given corpus.
	 * @param corpus
	 */
	public void train(C corpus);
	
	/**
	 * Get the trained pos-tagger, which has been created earlier by the method {@link #train(PosTagCorpus)}.
	 * @return
	 */
	public PosTagger getTrainedPosTagger();
	
	/**
	 * Save the pos-tagger model which has been learned earlier by the method {@link #train(PosTagCorpus)}. 
	 * @param modelDirectory
	 */
	public void save(File modelDirectory);

}
