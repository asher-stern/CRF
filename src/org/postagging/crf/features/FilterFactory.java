package org.postagging.crf.features;

import java.io.Serializable;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * Date: Nov 11, 2014
 *
 * @param <K>
 * @param <G>
 */
public interface FilterFactory<K, G> extends Serializable
{
	public Set<Filter<K, G>> createFilters(K token, G currentTag, G previousTag);
}
