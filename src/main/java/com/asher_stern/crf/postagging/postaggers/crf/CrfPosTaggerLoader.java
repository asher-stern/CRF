package com.asher_stern.crf.postagging.postaggers.crf;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

import com.asher_stern.crf.crf.CrfModel;
import com.asher_stern.crf.crf.run.CrfInferencePerformer;
import com.asher_stern.crf.postagging.postaggers.PosTaggerLoader;
import com.asher_stern.crf.utilities.CrfException;

/**
 * Loads a {@link CrfPosTagger} from a model that is stored in a directory in the file-system.
 * The model was earlier saved in the file-system by {@link CrfPosTaggerTrainer#save(File)}.
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public class CrfPosTaggerLoader implements PosTaggerLoader
{
	/*
	 * (non-Javadoc)
	 * @see org.postagging.postaggers.PosTaggerLoader#load(java.io.File)
	 */
	@Override
	public CrfPosTagger load(File directory)
	{
		if (!directory.exists()) {throw new CrfException("Given directory: "+directory.getAbsolutePath()+" does not exist.");}
		if (!directory.isDirectory()) {throw new CrfException("The loader requires a directory, but was provided with a file: "+directory.getAbsolutePath()+".");}
		
		try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(new File(directory, CrfPosTaggerTrainer.SAVE_LOAD_FILE_NAME ))))
		{
			@SuppressWarnings("unchecked")
			CrfModel<String, String> model = (CrfModel<String, String>) inputStream.readObject();
			return new CrfPosTagger(new CrfInferencePerformer<String, String>(model));
		}
		catch (IOException | ClassNotFoundException e)
		{
			throw new CrfException("Loading pos tagger failed.",e);
		}
	}

}
