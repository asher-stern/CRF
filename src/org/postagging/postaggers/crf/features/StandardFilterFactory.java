package org.postagging.postaggers.crf.features;

import java.util.LinkedHashSet;
import java.util.Set;

import org.postagging.crf.features.Filter;
import org.postagging.crf.features.FilterFactory;
import org.postagging.crf.features.TwoTagsFilter;

/**
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 */
public class StandardFilterFactory implements FilterFactory<String, String>
{
	private static final long serialVersionUID = 6283122214266870374L;

	@Override
	public Set<Filter<String, String>> createFilters(String token, String currentTag, String previousTag)
	{
		Set<Filter<String, String>> ret = new LinkedHashSet<Filter<String,String>>();
		ret.add(new TwoTagsFilter<String, String>(currentTag, previousTag));
		//ret.add(new TokenAndTagFilter<String, String>(token, currentTag));
		ret.add(new CaseInsensitiveTokenAndTagFilter(token, currentTag));
		return ret;
	}
}
