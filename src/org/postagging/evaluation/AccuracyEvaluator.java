package org.postagging.evaluation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.postagging.data.PosTagCorpusReader;
import org.postagging.data.TaggedToken;
import org.postagging.postaggers.PosTagger;
import org.postagging.utilities.PosTaggerException;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class AccuracyEvaluator
{
	public AccuracyEvaluator(PosTagCorpusReader corpus, PosTagger posTagger)
	{
		super();
		this.corpus = corpus;
		this.posTagger = posTagger;
	}

	public void evaluate()
	{
		correct = 0;
		incorrect = 0;
		accuracy = 0.0;
		
		while (corpus.hasNext())
		{
			List<TaggedToken> taggedSentence = corpus.next();
			List<String> sentence = taggedSentenceToSentence(taggedSentence);
			List<TaggedToken> taggedByPosTagger = posTagger.tagSentence(sentence);
			evaluateSentence(taggedSentence,taggedByPosTagger);
		}
		
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

	
	
	
	private void evaluateSentence(List<TaggedToken> taggedSentence, List<TaggedToken> taggedByPosTagger)
	{
		Iterator<TaggedToken> iteratorTaggedOriginal = taggedSentence.iterator();
		Iterator<TaggedToken> iteratorTaggedByPosTagger = taggedByPosTagger.iterator();
		while (iteratorTaggedOriginal.hasNext() && iteratorTaggedByPosTagger.hasNext())
		{
			TaggedToken original = iteratorTaggedOriginal.next();
			TaggedToken byPosTagger = iteratorTaggedByPosTagger.next();
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
	
	private List<String> taggedSentenceToSentence(List<TaggedToken> taggedSentence)
	{
		List<String> ret = new ArrayList<String>(taggedSentence.size());
		for (TaggedToken token : taggedSentence)
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

	
	private final PosTagCorpusReader corpus;
	private final PosTagger posTagger;
	
	private long correct = 0;
	private long incorrect = 0;
	private double accuracy = 0.0;
}
