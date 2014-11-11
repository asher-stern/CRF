package org.postagging.postaggers.crf.features;

import org.postagging.crf.CrfFeature;

import static org.postagging.utilities.PosTaggerUtilities.equalObjects;

/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class TokenAndTagFeature extends CrfFeature<String, String>
{
	public TokenAndTagFeature(String forToken, String forTag)
	{
		super();
		this.forToken = forToken;
		this.forTag = forTag;
	}
	
	
	@Override
	public double value(String[] sequence, int indexInSequence,
			String currentTag, String previousTag)
	{
		double ret = 0.0;
		if (equalObjects(sequence[indexInSequence],forToken) && equalObjects(currentTag,forTag))
		{
			ret = 1.0;
		}
		return ret;
	}
	
	
	@Override
	public String toString()
	{
		return "TokenAndTagFeature [forToken=" + forToken + ", forTag="
				+ forTag + "]";
	}
	
	




	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((forTag == null) ? 0 : forTag.hashCode());
		result = prime * result
				+ ((forToken == null) ? 0 : forToken.hashCode());
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
		TokenAndTagFeature other = (TokenAndTagFeature) obj;
		if (forTag == null)
		{
			if (other.forTag != null)
				return false;
		} else if (!forTag.equals(other.forTag))
			return false;
		if (forToken == null)
		{
			if (other.forToken != null)
				return false;
		} else if (!forToken.equals(other.forToken))
			return false;
		return true;
	}



	private final String forToken;
	private final String forTag;
}
