package org.postagging.postaggers.crf;

import java.util.Set;

import org.postagging.data.InMemoryPosTagCorpus;

/**
 * A factory which creates a {@link CrfPosTaggerFeatureGenerator}.
 * 
 * @author Asher Stern
 * Date: November 2014
 *
 */
public interface CrfPosTaggerFeatureGeneratorFactory
{
	/**
	 * Create the {@link CrfPosTaggerFeatureGenerator}.
	 * @param corpus
	 * @param tags
	 * @return
	 */
	public CrfPosTaggerFeatureGenerator create(InMemoryPosTagCorpus<String, String> corpus, Set<String> tags);
}
