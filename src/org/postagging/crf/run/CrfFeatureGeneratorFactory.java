package org.postagging.crf.run;

import java.util.List;
import java.util.Set;

import org.postagging.utilities.TaggedToken;

/**
 * A factory which creates a {@link CrfFeatureGenerator}.
 * 
 * @author Asher Stern
 * Date: November 2014
 *
 */
public interface CrfFeatureGeneratorFactory<K,G>
{
	/**
	 * Create the {@link CrfFeatureGenerator}.
	 * @param corpus
	 * @param tags
	 * @return
	 */
	public CrfFeatureGenerator<K,G> create(Iterable<? extends List<? extends TaggedToken<K, G> >> corpus, Set<G> tags);
}
