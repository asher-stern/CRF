package org.postagging.data;

import java.util.List;

import org.postagging.utilities.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public interface PosTagCorpus<K,G> extends Iterable<List<? extends TaggedToken<K, G> >>
{
	public PosTagCorpusReader<K,G> iterator();
}
