package org.postagging.postaggers.crf.features;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.postagging.crf.CrfFeature;
import org.postagging.data.InMemoryPosTagCorpus;
import org.postagging.postaggers.crf.CrfPosTaggerFeatureGenerator;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class StandardFeatureGenerator extends CrfPosTaggerFeatureGenerator
{

	public StandardFeatureGenerator(InMemoryPosTagCorpus<String, String> corpus, Set<String> tags)
	{
		super(corpus, tags);
	}

	@Override
	public void generateFeatures()
	{
		featuresAsList = null;
		features = new LinkedHashSet<CrfFeature<String,String>>();
		addTokenAndTagFeatures();
		addTagTransitionFeatures();
	}
	
	
	public ArrayList<CrfFeature<String, String>> getFeatures()
	{
		if (null==features) {throw new PosTaggerException("Features were not generated.");}
		
		featuresAsList = new ArrayList<CrfFeature<String,String>>(features.size());
		for (CrfFeature<String, String> feature : features)
		{
			featuresAsList.add(feature);
		}
		return featuresAsList;
	}
	
	
	private void addTokenAndTagFeatures()
	{
		for (List<? extends TaggedToken<String, String> > sentence : corpus)
		{
			for (TaggedToken<String, String> taggedToken : sentence)
			{
				TokenAndTagFeature feature = new TokenAndTagFeature(taggedToken.getToken(),taggedToken.getTag());
				features.add(feature);
			}
		}
	}
	
	private void addTagTransitionFeatures()
	{
		for (String tag : tags)
		{
			features.add(new TagTransitionFeature(null, tag));
			for (String previousTag : tags)
			{
				features.add(new TagTransitionFeature(previousTag, tag));
			}
		}
		
	}
	
	

	
	protected LinkedHashSet<CrfFeature<String, String>> features = null;
	protected ArrayList<CrfFeature<String, String>> featuresAsList = null;
}
