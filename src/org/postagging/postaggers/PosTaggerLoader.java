package org.postagging.postaggers;

import java.io.File;

/**
 * Creates a {@link PosTagger} by loading its model from a given directory in the file-system.
 * It is assumed that the model has been earlier stored in that directory by {@link PosTaggerTrainer#save(File)}.
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public interface PosTaggerLoader
{
	/**
	 * Load the {@link PosTagger} from the file-system and return it.
	 * @param directory A directory in the file-system which contains the model of the {@link PosTagger}.
	 * This model was earlier created and saved by {@link PosTaggerTrainer#save(File)}.
	 *  
	 * @return
	 */
	public PosTagger load(File directory);

}
