package org.crf.postagging.postaggers.crf.features;

import org.crf.crf.CrfFeature;

import static org.crf.utilities.PosTaggerUtilities.equalObjects;

/**
 * A feature that models the transition from one tag to another (possibly the same) tag.
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class TagTransitionFeature extends CrfFeature<String, String>
{
	private static final long serialVersionUID = -61200838311988363L;
	
	public TagTransitionFeature(String forPreviousTag, String forCurrentTag)
	{
		super();
		this.forPreviousTag = forPreviousTag;
		this.forCurrentTag = forCurrentTag;
	}
	
	@Override
	public double value(String[] sequence, int indexInSequence,
			String currentTag, String previousTag)
	{
		double ret = 0.0;
		if ( equalObjects(previousTag, forPreviousTag) && equalObjects(currentTag, forCurrentTag) )
		{
			ret = 1.0;
		}
		return ret;
	}
	
	
	
	@Override
	public String toString()
	{
		return "TagTransitionFeature [forPreviousTag=" + forPreviousTag
				+ ", forCurrentTag=" + forCurrentTag + "]";
	}
	
	
	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((forCurrentTag == null) ? 0 : forCurrentTag.hashCode());
		result = prime * result
				+ ((forPreviousTag == null) ? 0 : forPreviousTag.hashCode());
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
		TagTransitionFeature other = (TagTransitionFeature) obj;
		if (forCurrentTag == null)
		{
			if (other.forCurrentTag != null)
				return false;
		} else if (!forCurrentTag.equals(other.forCurrentTag))
			return false;
		if (forPreviousTag == null)
		{
			if (other.forPreviousTag != null)
				return false;
		} else if (!forPreviousTag.equals(other.forPreviousTag))
			return false;
		return true;
	}






	private final String forPreviousTag;
	private final String forCurrentTag;
}
