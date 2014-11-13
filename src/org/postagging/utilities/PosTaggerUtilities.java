package org.postagging.utilities;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.postagging.data.InMemoryPosTagCorpus;


/**
 * A collection of static helper functions for pos-tagger. 
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class PosTaggerUtilities
{
	public static Set<String> extractAllTagsFromCorpus(InMemoryPosTagCorpus<String, String> corpus)
	{
		Set<String> allTags = new LinkedHashSet<String>();
		for (List<? extends TaggedToken<String, String> > sentence : corpus)
		{
			for (TaggedToken<String, String> taggedToken : sentence)
			{
				allTags.add(taggedToken.getTag());
			}
		}
		return allTags;
	}
	
	public static <T> boolean equalObjects(T t1, T t2)
	{
		if (t1==t2) return true;
		if ( (t1==null) || (t2==null) ) return false;
		return t1.equals(t2);
	}

}
