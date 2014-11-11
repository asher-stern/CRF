package org.postagging.postaggers.crf.features;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.postagging.crf.features.CrfFilteredFeature;
import org.postagging.crf.features.TokenAndTagFilter;
import org.postagging.crf.features.TwoTagsFilter;
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
		setFilteredFeatures = new LinkedHashSet<CrfFilteredFeature<String,String>>();
		addTokenAndTagFeatures();
		addTagTransitionFeatures();
	}
	
	public Set<CrfFilteredFeature<String, String>> getFeatures()
	{
		if (null==setFilteredFeatures) {throw new PosTaggerException("Features were not generated.");}
		return setFilteredFeatures;
	}
	
	
	
	
	private void addTokenAndTagFeatures()
	{
		for (List<? extends TaggedToken<String, String> > sentence : corpus)
		{
			for (TaggedToken<String, String> taggedToken : sentence)
			{
				setFilteredFeatures.add(
						new CrfFilteredFeature<String, String>(
								new TokenAndTagFeature(taggedToken.getToken(),taggedToken.getTag()),
								new TokenAndTagFilter<String, String>(taggedToken.getToken(),taggedToken.getTag(), null),
								true
								)
						);
			}
		}
	}
	
	private void addTagTransitionFeatures()
	{
		for (String tag : tags)
		{
			setFilteredFeatures.add(
					new CrfFilteredFeature<String,String>(
							new TagTransitionFeature(null, tag),
							new TwoTagsFilter<String, String>(null, tag, null),
							true)
					);
			
			for (String previousTag : tags)
			{
				setFilteredFeatures.add(
						new CrfFilteredFeature<String,String>(
								new TagTransitionFeature(previousTag, tag),
								new TwoTagsFilter<String, String>(null, tag, previousTag),
								true)
						);

			}
		}
	}
	
	

	protected Set<CrfFilteredFeature<String, String>> setFilteredFeatures = null;
}
