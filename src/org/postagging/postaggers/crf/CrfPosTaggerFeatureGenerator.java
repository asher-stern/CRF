package org.postagging.postaggers.crf;

import java.util.ArrayList;
import java.util.Set;

import org.postagging.crf.CrfFeature;
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
	
	
	public abstract ArrayList<CrfFeature<String, String>> getFeatures();

	
	
	protected final InMemoryPosTagCorpus<String, String> corpus;
	protected final Set<String> tags;
}
