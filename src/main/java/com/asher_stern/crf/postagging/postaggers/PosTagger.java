package com.asher_stern.crf.postagging.postaggers;

import java.util.List;

import com.asher_stern.crf.utilities.TaggedToken;

/**
 * Assigns part-of-speech tags for a given sentence.
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public interface PosTagger
{
	/**
	 * Assigns tags for each token in the given sentence.
	 * @param sentence An input sentence, given as a list of tokens.
	 * @return The tagged sentence.
	 */
	public List<TaggedToken<String,String>> tagSentence(List<String> sentence);
}
