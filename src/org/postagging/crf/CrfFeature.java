package org.postagging.crf;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K>
 * @param <G>
 */
public interface CrfFeature<K,G> // K = token, G = tag
{
	public double value(K[] sequence, int indexInSequence, G currentTag, G previousTag);
}
