package org.postagging.lingpipe;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.postagging.data.PosTagCorpus;
import org.postagging.data.PosTagCorpusReader;
import org.postagging.data.TaggedToken;

import com.aliasi.corpus.Corpus;
import com.aliasi.corpus.ObjectHandler;
import com.aliasi.tag.Tagging;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public class LingPipeCorpusCreator
{
	public Corpus<ObjectHandler<Tagging<String>>> createTrainCorpus(final PosTagCorpus corpus)
	{
		return new Corpus<ObjectHandler<Tagging<String>>>()
		{
			@Override
		    public void visitTrain(ObjectHandler<Tagging<String>> handler) throws IOException
			{
				visit(handler,corpus);
		    }
			@Override
		    public void visitTest(ObjectHandler<Tagging<String>> handler) {} // no operation


		};
	}
	

	public Corpus<ObjectHandler<Tagging<String>>> createTestCorpus(final PosTagCorpus corpus)
	{
		return new Corpus<ObjectHandler<Tagging<String>>>()
		{
			@Override
		    public void visitTrain(ObjectHandler<Tagging<String>> handler) {} // no operation

			@Override
		    public void visitTest(ObjectHandler<Tagging<String>> handler) throws IOException
			{
				visit(handler,corpus);
		    }

		};
	}

	
	
	private static void visit(final ObjectHandler<Tagging<String>> handler, final PosTagCorpus corpus)
	{
		PosTagCorpusReader reader = corpus.createReader();
		while (reader.hasNext())
		{
			List<TaggedToken> sentence = reader.next();
			List<String> tokens = new ArrayList<String>(sentence.size());
			List<String> tags = new ArrayList<String>(sentence.size());
			for (TaggedToken taggedToken : sentence)
			{
				tokens.add(taggedToken.getToken());
				tags.add(taggedToken.getTag());
			}
			Tagging<String> tagging = new Tagging<String>(tokens,tags);
			handler.handle(tagging);
		}
	}
}
