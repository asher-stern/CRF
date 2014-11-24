package org.crf.postagging.postaggers.crf.features;

import org.crf.crf.filters.Filter;

/**
 * A {@link Filter} for {@link CaseInsensitiveTokenAndTagFeature}.
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public class CaseInsensitiveTokenAndTagFilter extends Filter<String, String>
{
	private static final long serialVersionUID = -4114831547683222843L;
	
	public CaseInsensitiveTokenAndTagFilter(String token, String tag)
	{
		super();
		this.token_lowerCase = ( (token==null)?null:token.toLowerCase() );
		this.tag = tag;
	}
	
	
	
	
	@Override
	public int hashCode()
	{
		if (hashCodeCalculated)
		{
			return hashCodeValue;
		}
		else
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((tag == null) ? 0 : tag.hashCode());
			result = prime * result
					+ ((token_lowerCase == null) ? 0 : token_lowerCase.hashCode());
			hashCodeValue = result;
			hashCodeCalculated = true;
			return result;
		}
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CaseInsensitiveTokenAndTagFilter other = (CaseInsensitiveTokenAndTagFilter) obj;
		if (tag == null)
		{
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (token_lowerCase == null)
		{
			if (other.token_lowerCase != null)
				return false;
		} else if (!token_lowerCase.equals(other.token_lowerCase))
			return false;
		return true;
	}




	private final String token_lowerCase;
	private final String tag;
	
	private transient int hashCodeValue = 0;
	private transient boolean hashCodeCalculated = false;
}
