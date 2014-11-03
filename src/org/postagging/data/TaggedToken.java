package org.postagging.data;

/**
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class TaggedToken
{
	public TaggedToken(String token, String tag)
	{
		super();
		this.token = token;
		this.tag = tag;
	}
	
	
	
	public String getToken()
	{
		return token;
	}
	public String getTag()
	{
		return tag;
	}
	
	
	



	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((tag == null) ? 0 : tag.hashCode());
		result = prime * result + ((token == null) ? 0 : token.hashCode());
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
		TaggedToken other = (TaggedToken) obj;
		if (tag == null)
		{
			if (other.tag != null)
				return false;
		} else if (!tag.equals(other.tag))
			return false;
		if (token == null)
		{
			if (other.token != null)
				return false;
		} else if (!token.equals(other.token))
			return false;
		return true;
	}


	




	@Override
	public String toString()
	{
		return "TaggedToken [getToken()=" + getToken() + ", getTag()="
				+ getTag() + "]";
	}







	private final String token;
	private final String tag;
}
