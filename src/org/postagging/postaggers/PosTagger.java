package org.postagging.postaggers;

import java.util.List;

import org.postagging.data.StringTaggedToken;

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
	public List<StringTaggedToken> tagSentence(List<String> sentence);
}
