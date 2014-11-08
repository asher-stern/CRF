package org.postagging.data;

import org.postagging.crf.CrfTaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class TaggedToken extends CrfTaggedToken<String, String>
{
	public TaggedToken(String token, String tag)
	{
		super(token, tag);
	}
}
