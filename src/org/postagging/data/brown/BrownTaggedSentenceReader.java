package org.postagging.data.brown;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

/**
 * Sentence reader for Brown corpus.
 * Brown corpus can be downloaded from: http://www.nltk.org/nltk_data/
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class BrownTaggedSentenceReader
{
	public static final String SEPARATOR = "/";
	public static final String PUNC_TAG = "PUNC";
	public static final char SUBTYPE_INDICATOR = '-';
	public static final char SUBTYPE_INDICATOR2 = '+';
	public static final char POSSESSIVE_INDICATOR = '$';
	public static final Set<String> TAGS;
	public static final Set<String> IGNORE;
	

	static
	{
		TAGS = new LinkedHashSet<String>();
		TAGS.add("*");
		TAGS.add("ABL");
		TAGS.add("ABN");
		TAGS.add("ABX");
		TAGS.add("AP");
		TAGS.add("AT");
		TAGS.add("BE");
		TAGS.add("BED");
		TAGS.add("BEDZ");
		TAGS.add("BEG");
		TAGS.add("BEM");
		TAGS.add("BEN");
		TAGS.add("BER");
		TAGS.add("BEZ");
		TAGS.add("CC");
		TAGS.add("CD");
		TAGS.add("CS");
		TAGS.add("DO");
		TAGS.add("DOD");
		TAGS.add("DOZ");
		TAGS.add("DT");
		TAGS.add("DTI");
		TAGS.add("DTS");
		TAGS.add("DTX");
		TAGS.add("EX");
		TAGS.add("FW");
		TAGS.add("HL");
		TAGS.add("HV");
		TAGS.add("HVD");
		TAGS.add("HVG");
		TAGS.add("HVN");
		TAGS.add("HVZ");
		TAGS.add("IN");
		TAGS.add("JJ");
		TAGS.add("JJR");
		TAGS.add("JJS");
		TAGS.add("JJT");
		TAGS.add("MD");
		TAGS.add("NC");
		TAGS.add("NN");
		//TAGS.add("NN$");
		TAGS.add("NNS");
		//TAGS.add("NNS$");
		TAGS.add("NP");
		//TAGS.add("NP$");
		TAGS.add("NPS");
		//TAGS.add("NPS$");
		TAGS.add("NR");
		TAGS.add("NRS");
		TAGS.add("OD");
		TAGS.add("PN");
		//TAGS.add("PN$");
		TAGS.add("PP"); // Not in the original list. Add for PP$ and PP$$, which do appear in the original list. 
		//TAGS.add("PP$");
		//TAGS.add("PP$$");
		TAGS.add("PPL");
		TAGS.add("PPLS");
		TAGS.add("PPO");
		TAGS.add("PPS");
		TAGS.add("PPSS");
		TAGS.add("QL");
		TAGS.add("QLP");
		TAGS.add("RB");
		TAGS.add("RBR");
		TAGS.add("RBT");
		TAGS.add("RN");
		TAGS.add("RP");
		TAGS.add("TL");
		TAGS.add("TO");
		TAGS.add("UH");
		TAGS.add("VB");
		TAGS.add("VBD");
		TAGS.add("VBG");
		TAGS.add("VBN");
		TAGS.add("VBZ");
		TAGS.add("WDT");
		TAGS.add("WP"); // Not in the original list. Add for WP$ which does appear in the original list.
		//TAGS.add("WP$"); 
		TAGS.add("WPO");
		TAGS.add("WPS");
		TAGS.add("WQL");
		TAGS.add("WRB");
		
		IGNORE = new LinkedHashSet<String>();
		IGNORE.add("NIL");
	}


	

	public BrownTaggedSentenceReader(String annotatedSentence)
	{
		this.annotatedSentence = annotatedSentence;
	}

	public List<TaggedToken<String,String>> read()
	{
		String[] tokens = annotatedSentence.split("\\s+");
		List<TaggedToken<String,String>> ret = new ArrayList<TaggedToken<String,String>>(tokens.length);
		
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
					if (tag!=null)
					{
						ret.add(new TaggedToken<String,String>(tokenWord, tag));
					}
				}
			}
		}
		return ret;
	}
	
	
	
	private String normalizeTag(String tag)
	{
		final String originalTag = tag;
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
		if (tag.startsWith("--"))
		{
			tag = "--";
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
		
		boolean shouldBeIgnored = false;
		String ret = null;
		tag = tag.toUpperCase();
		if (letterDetected)
		{
			int starIndex = tag.indexOf('*');
			if (starIndex>=1)
			{
				tag = tag.substring(0, starIndex);
			}
			int possessiveIndex = tag.indexOf(POSSESSIVE_INDICATOR);
			if (possessiveIndex>=1)
			{
				tag = tag.substring(0, possessiveIndex);
			}

			if (IGNORE.contains(tag))
			{
				shouldBeIgnored = true;
				ret = null;
			}
			if (TAGS.contains(tag))
			{
				ret = tag;
			}
		}
		else
		{
			ret = PUNC_TAG;
		}
		if ( (ret == null) && (!shouldBeIgnored) )
		{
			throw new PosTaggerException("Unrecognized tag in Brown corpus: "+tag+". Original tag: "+originalTag+".\n"
					+ "Sentence: "+annotatedSentence);
		}
		
		return ret;
	}
	
	protected final String annotatedSentence;
}
