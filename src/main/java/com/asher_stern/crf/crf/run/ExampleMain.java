package com.asher_stern.crf.crf.run;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.asher_stern.crf.crf.CrfModel;
import com.asher_stern.crf.crf.filters.FilterFactory;
import com.asher_stern.crf.utilities.TaggedToken;


/**
 * An example for usage of CRF. Don't run this class, but use it as a template for writing your own main class.
 * 
 * @author Asher Stern
 * Date: Nov 23, 2014
 *
 */
public class ExampleMain
{

	@SuppressWarnings("unchecked")
	public static void main(String[] args)
	{
		// Load a corpus into the memory
		List<List<? extends TaggedToken<String, String> >> corpus = loadCorpusWhereTokensAndTagsAreStrings();
		
		// Create trainer factory
		CrfTrainerFactory<String, String> trainerFactory = new CrfTrainerFactory<String, String>();
		
		// Create trainer
		CrfTrainer<String,String> trainer = trainerFactory.createTrainer(
				corpus,
				(Iterable<? extends List<? extends TaggedToken<String, String> >> theCorpus, Set<String> tags) -> createFeatureGeneratorForGivenCorpus(theCorpus,tags),
				createFilterFactory());

		// Run training with the loaded corpus.
		trainer.train(corpus);
		
		// Get the model
		CrfModel<String, String> crfModel = trainer.getLearnedModel();
		
		// Save the model into the disk.
		File file = new File("example.ser");
		save(crfModel,file);
		
		////////
		
		// Later... Load the model from the disk
		crfModel = (CrfModel<String, String>) load(file);
		
		// Create a CrfInferencePerformer, to find tags for test data
		CrfInferencePerformer<String, String> inferencePerformer = new CrfInferencePerformer<String, String>(crfModel);
		
		// Test:
		List<String> test = Arrays.asList( "This is a sequence for test data".split("\\s+") );
		List<TaggedToken<String, String>> result = inferencePerformer.tagSequence(test);
		
		// Print the result:
		for (TaggedToken<String, String> taggedToken : result)
		{
			System.out.println("Tag for: "+taggedToken.getToken()+" is "+taggedToken.getTag());
		}
	}
	
	
	public static List<List<? extends TaggedToken<String, String> >> loadCorpusWhereTokensAndTagsAreStrings()
	{
		// Implement this method
		return null;
	}
	
	public static CrfFeatureGenerator<String, String> createFeatureGeneratorForGivenCorpus(Iterable<? extends List<? extends TaggedToken<String, String> >> corpus, Set<String> tags)
	{
		// Implement this method
		return null;
	}
	
	public static FilterFactory<String, String> createFilterFactory()
	{
		// Implement this method
		return null;
	}
	
	public static void save(Object object, File file)
	{
		try(ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(file)))
		{
			stream.writeObject(object);
		}
		catch (IOException e)
		{
			throw new RuntimeException("Failed to save",e);
		}
	}
	
	public static Object load(File file)
	{
		try(ObjectInputStream stream = new ObjectInputStream(new FileInputStream(file)))
		{
			return stream.readObject();
		}
		catch (ClassNotFoundException | IOException e)
		{
			throw new RuntimeException("Failed to load",e);
		}
	}
		

}
