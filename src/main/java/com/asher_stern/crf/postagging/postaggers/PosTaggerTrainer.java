package com.asher_stern.crf.postagging.postaggers;

import java.io.File;
import java.util.List;

import com.asher_stern.crf.utilities.TaggedToken;

/**
 * Trains a {@link PosTagger} with the given corpus, and provides method to get the trained pos-tagger and
 * saving its model in the file-system.
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public interface PosTaggerTrainer<C extends Iterable<? extends List<? extends TaggedToken<String, String>>>>
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
