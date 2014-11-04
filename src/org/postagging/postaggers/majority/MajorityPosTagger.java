package org.postagging.postaggers.majority;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.postagging.data.TaggedToken;
import org.postagging.postaggers.PosTagger;

/**
 * 
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
	public List<TaggedToken> tagSentence(List<String> sentence)
	{
		List<TaggedToken> ret = new ArrayList<TaggedToken>(sentence.size());
		for (String token : sentence)
		{
			String tag = majorityMap.get(token);
			if (null==tag)
			{
				tag = generalMajorTag;
			}
			
			ret.add(new TaggedToken(token, tag));
		}
		
		return ret;
	}
	
	
	private final Map<String, String> majorityMap;
	private final String generalMajorTag;
}
