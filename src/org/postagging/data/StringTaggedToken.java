package org.postagging.data;

import org.postagging.utilities.TaggedToken;

/**
 * A token and a tag, both represented as strings.
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class StringTaggedToken extends TaggedToken<String, String>
{
	public StringTaggedToken(String token, String tag)
	{
		super(token, tag);
	}
}
