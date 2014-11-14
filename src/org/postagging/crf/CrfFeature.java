package org.postagging.crf;

/**
 * A CRF feature.
 * In CRF, a feature is a function that has the input (x,j,s,s') where: x is the sentence (sequence),
 * j is a position in that sequence, s is the tag of the token j, and s' is the tag of the token j-1.
 * <P>
 * {@link CrfFeature} must implement {@link #equals(Object)} and {@link #hashCode()}.
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K> token
 * @param <G> tag
 */
public abstract class CrfFeature<K,G> // K = token, G = tag
{
	public abstract double value(K[] sequence, int indexInSequence, G currentTag, G previousTag);
	
	public abstract boolean equals(Object obj);
	public abstract int hashCode();
}
