package org.postagging.evaluation;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.postagging.postaggers.PosTagger;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class AccuracyEvaluator
{
	public AccuracyEvaluator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, PosTagger posTagger)
	{
		this(corpus,posTagger,null);
	}

	
	public AccuracyEvaluator(Iterable<? extends List<? extends TaggedToken<String, String>>> corpus, PosTagger posTagger, PrintWriter taggedTestWriter)
	{
		super();
		this.corpus = corpus;
		this.posTagger = posTagger;
		this.taggedTestWriter = taggedTestWriter;
	}

	
	public void evaluate()
	{
		correct = 0;
		incorrect = 0;
		accuracy = 0.0;
		Iterator<? extends List<? extends TaggedToken<String, String>>> reader = corpus.iterator();
		
		int debug_index=0;
		while (reader.hasNext())
		{
			List<? extends TaggedToken<String, String>> taggedSentence = reader.next();
			++debug_index;
			List<String> sentence = taggedSentenceToSentence(taggedSentence);
			List<TaggedToken<String,String>> taggedByPosTagger = posTagger.tagSentence(sentence);
			evaluateSentence(taggedSentence,taggedByPosTagger);
			
			if (taggedTestWriter!=null)
			{
				taggedTestWriter.println(printSentence(taggedSentence));
				taggedTestWriter.println(printSentence(taggedByPosTagger));
			}
			
			if (logger.isDebugEnabled())
			{
				if ((debug_index%100)==0){logger.debug("Evaluated: "+debug_index);}	
			}
		}
		if (taggedTestWriter!=null) {taggedTestWriter.flush();}
		
		accuracy = ((double)correct)/((double)(correct+incorrect));
	}
	
	public long getCorrect()
	{
		return correct;
	}

	public long getIncorrect()
	{
		return incorrect;
	}

	public double getAccuracy()
	{
		return accuracy;
	}

	
	
	
	private void evaluateSentence(List<? extends TaggedToken<String,String>> taggedSentence, List<TaggedToken<String,String>> taggedByPosTagger)
	{
		Iterator<? extends TaggedToken<String,String>> iteratorTaggedOriginal = taggedSentence.iterator();
		Iterator<? extends TaggedToken<String,String>> iteratorTaggedByPosTagger = taggedByPosTagger.iterator();
		while (iteratorTaggedOriginal.hasNext() && iteratorTaggedByPosTagger.hasNext())
		{
			TaggedToken<String,String> original = iteratorTaggedOriginal.next();
			TaggedToken<String,String> byPosTagger = iteratorTaggedByPosTagger.next();
			if (!(equals(original.getToken(),byPosTagger.getToken()))) {throw new PosTaggerException("Tokens not equal in evaluation.");}
			if (equals(original.getTag(),byPosTagger.getTag()))
			{
				++correct;
			}
			else
			{
				++incorrect;
			}
		}
		if (iteratorTaggedOriginal.hasNext() || iteratorTaggedByPosTagger.hasNext())
		{
			throw new PosTaggerException("Sentences sizes are not equal in evaluation.");
		}
	}
	
	private String printSentence(List<? extends TaggedToken<String,String>> taggedSentence)
	{
		StringBuilder sb = new StringBuilder();
		for (TaggedToken<String,String> taggedToken : taggedSentence)
		{
			sb.append(taggedToken.getToken()).append("/").append( String.format("%-4s", taggedToken.getTag()) ).append(" ");
		}
		return sb.toString();
	}
	
	private List<String> taggedSentenceToSentence(List<? extends TaggedToken<String, String>> taggedSentence)
	{
		List<String> ret = new ArrayList<String>(taggedSentence.size());
		for (TaggedToken<String, String> token : taggedSentence)
		{
			ret.add(token.getToken());
		}
		return ret;
	}
	
	private static boolean equals(Object object1, Object object2)
	{
		if (object1==object2) return true;
		else if ( (object1==null) || (object2==null) ) return false;
		return object1.equals(object2);
	}

	
	private final Iterable<? extends List<? extends TaggedToken<String, String>>> corpus;
	private final PosTagger posTagger;
	private final PrintWriter taggedTestWriter;
	
	private long correct = 0;
	private long incorrect = 0;
	private double accuracy = 0.0;
	
	private static final Logger logger = Logger.getLogger(AccuracyEvaluator.class);
}
