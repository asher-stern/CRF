package org.postagging.postaggers.crf;

import java.util.ArrayList;
import java.util.Set;

import org.postagging.crf.CrfFeature;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.postaggers.crf.features.StandardFeatureGenerator;
import org.postagging.utilities.PosTaggerUtilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class CrfPosTaggerTrainerFactory
{
	public CrfPosTaggerTrainer createPosTaggerTrainer(InMemoryPosTagCorpus<String, String> corpus)
	{
		return createPosTaggerTrainer(corpus,
				new CrfPosTaggerFeatureGeneratorFactory()
				{
					@Override
					public CrfPosTaggerFeatureGenerator create(InMemoryPosTagCorpus<String, String> corpus, Set<String> tags)
					{
						return new StandardFeatureGenerator(corpus, tags);
					}
				}
		);
	}
	
	public CrfPosTaggerTrainer createPosTaggerTrainer(InMemoryPosTagCorpus<String, String> corpus, CrfPosTaggerFeatureGeneratorFactory featureGeneratorFactory)
	{
		Set<String> tags = PosTaggerUtilities.extractAllTagsFromCorpus(corpus);
		CrfPosTaggerFeatureGenerator featureGenerator = featureGeneratorFactory.create(corpus, tags);
		featureGenerator.generateFeatures();
		ArrayList<CrfFeature<String, String>> features = featureGenerator.getFeatures();
		
		return new CrfPosTaggerTrainer(features,tags);
	}
	

}
