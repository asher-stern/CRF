package org.postagging.postaggers.crf;

import java.util.Set;

import org.postagging.data.InMemoryPosTagCorpus;

public interface CrfPosTaggerFeatureGeneratorFactory
{
	public CrfPosTaggerFeatureGenerator create(InMemoryPosTagCorpus<String, String> corpus, Set<String> tags);
}
