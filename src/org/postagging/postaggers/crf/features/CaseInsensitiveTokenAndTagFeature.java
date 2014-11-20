package org.postagging.postaggers.crf.features;

import org.postagging.crf.CrfFeature;

import static org.postagging.utilities.PosTaggerUtilities.equalObjects;

/**
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public class CaseInsensitiveTokenAndTagFeature extends CrfFeature<String, String>
{
	private static final long serialVersionUID = 2767104089617969815L;
	
	public CaseInsensitiveTokenAndTagFeature(String token, String tag)
	{
		super();
		this.tokenLowerCase = ( (token==null)?null:token.toLowerCase() );
		this.tag = tag;
	}

	@Override
	public double value(String[] sequence, int indexInSequence,
			String currentTag, String previousTag)
	{
		String tokenInSentence_lowerCase = sequence[indexInSequence];
		if (tokenInSentence_lowerCase!=null)
		{
			tokenInSentence_lowerCase = tokenInSentence_lowerCase.toLowerCase();
		}
		
		double ret = 0.0;
		if ( equalObjects(tokenInSentence_lowerCase, tokenLowerCase) && equalObjects(currentTag, tag))
		{
			ret = 1.0;
		}
		return ret;
	}


	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result
				+ ((tokenLowerCase == null) ? 0 : tokenLowerCase.hashCode());
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
		CaseInsensitiveTokenAndTagFeature other = (CaseInsensitiveTokenAndTagFeature) obj;
		if (tag == null)
		{
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (tokenLowerCase == null)
		{
			if (other.tokenLowerCase != null)
				return false;
		} else if (!tokenLowerCase.equals(other.tokenLowerCase))
			return false;
		return true;
	}


	



	@Override
	public String toString()
	{
		return "CaseInsensitiveTokenAndTagFeature [tokenLowerCase="
				+ tokenLowerCase + ", tag=" + tag + "]";
	}






	private final String tokenLowerCase;
	private final String tag;
}
