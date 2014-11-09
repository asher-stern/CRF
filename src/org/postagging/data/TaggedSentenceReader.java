package org.postagging.data;

import java.util.List;

/**
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public abstract class TaggedSentenceReader
{
	
	public TaggedSentenceReader(String annotatedSentence)
	{
		super();
		this.annotatedSentence = annotatedSentence;
	}

	public abstract List<StringTaggedToken> read();
	
	protected final String annotatedSentence;
}
