package org.postagging.crf;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K>
 * @param <G>
 */
public abstract class CrfFeature<K,G> // K = token, G = tag
{
	public abstract double value(K[] sequence, int indexInSequence, G currentTag, G previousTag);
	
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
}
