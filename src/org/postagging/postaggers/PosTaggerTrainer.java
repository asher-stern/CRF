package org.postagging.postaggers;

import java.io.File;

import org.postagging.data.PosTagCorpus;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public interface PosTaggerTrainer
{
	public void train(PosTagCorpus corpus);
	
	public PosTagger getTrainedPosTagger();
	
	public void save(File modelDirectory);

}
