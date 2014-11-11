package org.postagging.postaggers.crf;


import java.util.Set;

import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.data.InMemoryPosTagCorpus;

/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public abstract class CrfPosTaggerFeatureGenerator
{
	public CrfPosTaggerFeatureGenerator(InMemoryPosTagCorpus<String, String> corpus, Set<String> tags)
	{
		super();
		this.corpus = corpus;
		this.tags = tags;
	}
	
	public abstract void generateFeatures();
	
	
	public abstract Set<CrfFilteredFeature<String, String>> getFeatures();

	
	
	protected final InMemoryPosTagCorpus<String, String> corpus;
	protected final Set<String> tags;
}
