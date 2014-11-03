package org.postagging.data.brown;

import java.util.ArrayList;
import java.util.List;

import org.postagging.data.TaggedSentenceReader;
import org.postagging.data.TaggedToken;

/**
 * Sentence reader for Brown corpus.
 * Brown corpus can be downloaded from: http://www.nltk.org/nltk_data/
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class BrownTaggedSentenceReader extends TaggedSentenceReader
{
	public static final String SEPARATOR = "/";
	public static final String PUNC_TAG = "PUNC";
	public static final char SUBTYPE_INDICATOR = '-';
	public static final char SUBTYPE_INDICATOR2 = '+';

	public BrownTaggedSentenceReader(String annotatedSentence)
	{
		super(annotatedSentence);
	}

	@Override
	public List<TaggedToken> read()
	{
		String[] tokens = annotatedSentence.split("\\s+");
		List<TaggedToken> ret = new ArrayList<TaggedToken>(tokens.length);
		
		for (String token : tokens)
		{
			if (token.length()>0)
			{
				String[] tokenAndTag = token.split(SEPARATOR);
				if (tokenAndTag.length==(1+1))
				{
					String tokenWord = tokenAndTag[0].trim();
					String tag = tokenAndTag[1].trim();
					tag = normalizeTag(tag);
					ret.add(new TaggedToken(tokenWord, tag));
				}
			}
		}
		return ret;
	}
	
	
	
	private String normalizeTag(String tag)
	{
		int subtypeIndex = tag.indexOf(SUBTYPE_INDICATOR);
		if (subtypeIndex>0)
		{
			tag = tag.substring(0, subtypeIndex);
		}
		int subtype2Index = tag.indexOf(SUBTYPE_INDICATOR2);
		if (subtype2Index>0)
		{
			tag = tag.substring(0, subtype2Index);
		}
		
		boolean letterDetected = false;
		for (char c : tag.toCharArray())
		{
			if (Character.isAlphabetic(c))
			{
				letterDetected = true;
			}
			if (c=='*')
			{
				letterDetected = true;
			}
			if (letterDetected) {break;}
		}
		
		if (letterDetected)
		{
			return tag;
		}
		else
		{
			return PUNC_TAG;
		}
		
	}
	

}
