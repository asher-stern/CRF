package org.postagging.data;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public interface PosTagCorpus<K,G>
{
	public PosTagCorpusReader<K,G> iterator();
}
