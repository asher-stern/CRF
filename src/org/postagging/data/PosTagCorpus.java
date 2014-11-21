package org.postagging.data;

import java.util.List;

import org.postagging.utilities.TaggedToken;

/**
 * A corpus of pos-tagged sentences. It contains sentences, where each token has a part-of-speech tag
 * assigned to it.
 * 
 * @author Asher Stern
 * Date: Nov 5, 2014
 *
 */
public interface PosTagCorpus<K,G> extends Iterable<List<? extends TaggedToken<K, G> >>
{
	public PosTagCorpusReader<K,G> iterator();
}
