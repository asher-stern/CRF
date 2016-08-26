package com.asher_stern.crf.crf.filters;

/**
 * A filter which filters only by the tag of the token.
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K>
 * @param <G>
 */
public class TagFilter<K,G> extends Filter<K, G>
{
	private static final long serialVersionUID = 3624873223333620234L;

	
	public TagFilter(G currentTag)
	{
		this.currentTag = currentTag;
	}
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentTag == null) ? 0 : currentTag.hashCode());
		return result;
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
		TagFilter<?,?> other = (TagFilter<?,?>) obj;
		if (currentTag == null)
		{
			if (other.currentTag != null)
				return false;
		} else if (!currentTag.equals(other.currentTag))
			return false;
		return true;
	}



	private final G currentTag;
}
