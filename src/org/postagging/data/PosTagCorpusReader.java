package org.postagging.data;

import java.util.Iterator;
import java.util.List;

import org.postagging.utilities.TaggedToken;

/**
 * An iterator of pos-tagged sentences.
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public interface PosTagCorpusReader<K,G> extends Iterator<List<? extends TaggedToken<K, G> >>
{
}
