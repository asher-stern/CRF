package org.postagging.crf.features;

/**
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K>
 * @param <G>
 */
public class TwoTagsFilter<K, G> extends Filter<K, G>
{
	private static final long serialVersionUID = -4850947891058236177L;
	
	public TwoTagsFilter(G currentTag, G previousTag)
	{
		this.currentTag = currentTag;
		this.previousTag = previousTag;
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
			result = prime * result
					+ ((currentTag == null) ? 0 : currentTag.hashCode());
			result = prime * result
					+ ((previousTag == null) ? 0 : previousTag.hashCode());
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
		TwoTagsFilter<?,?> other = (TwoTagsFilter<?,?>) obj;
		if (currentTag == null)
		{
			if (other.currentTag != null)
				return false;
		} else if (!currentTag.equals(other.currentTag))
			return false;
		if (previousTag == null)
		{
			if (other.previousTag != null)
				return false;
		} else if (!previousTag.equals(other.previousTag))
			return false;
		return true;
	}



	private final G currentTag;
	private final G previousTag;
	
	private transient int hashCodeValue = 0;
	private transient boolean hashCodeCalculated = false;
}
