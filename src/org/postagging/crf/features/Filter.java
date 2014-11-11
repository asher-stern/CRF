package org.postagging.crf.features;

public abstract class Filter<K,G>
{
	public Filter(K token, G currentTag, G previousTag)
	{
	}
	
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
