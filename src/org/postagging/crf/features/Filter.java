package org.postagging.crf.features;

import java.io.Serializable;

/**
 * 
 * @author Asher Stern
 * Date: November 2014
 *
 * @param <K>
 * @param <G>
 */
public abstract class Filter<K,G> implements Serializable
{
	private static final long serialVersionUID = 5563671313834518710L;
	
	public abstract int hashCode();
	public abstract boolean equals(Object obj);
}
