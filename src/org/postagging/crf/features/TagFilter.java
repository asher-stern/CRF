package org.postagging.crf.features;

/**
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K>
 * @param <G>
 */
public class TagFilter<K,G> extends Filter<K, G>
{
	public TagFilter(K token, G currentTag, G previousTag)
	{
		super(token, currentTag, previousTag);
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
