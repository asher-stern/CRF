package org.postagging.postaggers.majority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.postagging.postaggers.PosTagger;
import org.postagging.utilities.TaggedToken;

/**
 * A {@link PosTagger} which assigns for each word the tag that occurs mostly with that word.
 * For words that were not seen in the training corpus, this pos-tagger assigns that tag that is most frequent in the
 * training corpus.
 * @author Asher Stern
 * Date: Nov 4, 2014
 *
 */
public class MajorityPosTagger implements PosTagger
{
	public MajorityPosTagger(Map<String, String> majorityMap,
			String generalMajorTag)
	{
		super();
		this.majorityMap = majorityMap;
		this.generalMajorTag = generalMajorTag;
	}
	
	
	@Override
	public List<TaggedToken<String,String>> tagSentence(List<String> sentence)
	{
		List<TaggedToken<String,String>> ret = new ArrayList<TaggedToken<String,String>>(sentence.size());
		for (String token : sentence)
		{
			String tag = majorityMap.get(token);
			if (null==tag)
			{
				tag = generalMajorTag;
			}
			
			ret.add(new TaggedToken<String,String>(token, tag));
		}
		
		return ret;
	}
	
	
	private final Map<String, String> majorityMap;
	private final String generalMajorTag;
}
