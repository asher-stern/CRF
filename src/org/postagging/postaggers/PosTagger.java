package org.postagging.postaggers;

import java.util.List;

import org.postagging.data.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public interface PosTagger
{
	public List<TaggedToken> tagSentence(List<String> sentence);
}
