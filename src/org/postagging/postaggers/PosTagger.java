package org.postagging.postaggers;

import java.util.List;

import org.postagging.data.StringTaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public interface PosTagger
{
	public List<StringTaggedToken> tagSentence(List<String> sentence);
}
