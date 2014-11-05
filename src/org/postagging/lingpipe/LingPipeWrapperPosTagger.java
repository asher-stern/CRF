package org.postagging.lingpipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.postagging.data.TaggedToken;
import org.postagging.postaggers.PosTagger;
import org.postagging.utilities.PosTaggerException;

import com.aliasi.crf.ChainCrf;
import com.aliasi.tag.Tagging;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public class LingPipeWrapperPosTagger implements PosTagger
{
	public LingPipeWrapperPosTagger(ChainCrf<String> lingPipeCrf)
	{
		super();
		this.lingPipeCrf = lingPipeCrf;
	}

	@Override
	public List<TaggedToken> tagSentence(List<String> sentence)
	{
		// Call LingPipe tagger
		Tagging<String> tagging = lingPipeCrf.tag(sentence);
		
		// Take the output of LingPipe tagger
		List<String> tokens = tagging.tokens();
		List<String> tags = tagging.tags();
		if (tokens.size()!=sentence.size()) {throw new PosTaggerException("LingPipe tokens list differs in size from sentence size.");}
		if (tags.size()!=sentence.size()) {throw new PosTaggerException("LingPipe tags list differs in size from sentence size.");}
		
		// Build the output of this method
		List<TaggedToken> ret = new ArrayList<TaggedToken>(tokens.size());
		Iterator<String> tokensIterator = tokens.iterator();
		Iterator<String> tagsIterator = tags.iterator();
		while (tokensIterator.hasNext()&&tagsIterator.hasNext())
		{
			String token = tokensIterator.next();
			String tag = tagsIterator.next();
			
			ret.add(new TaggedToken(token, tag));
		}
		if (tokensIterator.hasNext()||tagsIterator.hasNext()) {throw new PosTaggerException("LingPipe Crf tagging lists are not of the same size.");}
		
		return ret;
	}
	
	private final ChainCrf<String> lingPipeCrf;
}
