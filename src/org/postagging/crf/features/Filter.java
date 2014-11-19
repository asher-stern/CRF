package org.postagging.crf.features;

/**
 * 
 * @author Asher Stern
 * Date: November 2014
 *
 * @param <K>
 * @param <G>
 */
public abstract class Filter<K,G>
{
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
