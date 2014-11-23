package org.postagging.crf.run;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.postagging.crf.CrfInference;
import org.postagging.crf.CrfInferenceViterbi;
import org.postagging.crf.CrfModel;
import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.TaggedToken;

/**
 * 
 * @author Asher Stern
 * Date: Nov 23, 2014
 *
 * @param <K>
 * @param <G>
 */
public class CrfInferencePerformer<K, G>
{
	public CrfInferencePerformer(CrfModel<K, G> model)
	{
		super();
		this.model = model;
	}

	public List<TaggedToken<K,G>> tagSequence(List<K> sequence)
	{
		if (null==sequence) {return null;}
		if (sequence.size()==0) {return Collections.<TaggedToken<K,G>>emptyList();}
		
		@SuppressWarnings("unchecked")
		K[] sentenceAsArray = sequence.toArray( (K[]) Array.newInstance(sequence.get(0).getClass(), sequence.size()) );
		CrfInference<K, G> crfInference = new CrfInferenceViterbi<K, G>(model, sentenceAsArray);
		G[] bestTags = crfInference.inferBestTagSequence();

		if (sentenceAsArray.length!=bestTags.length) {throw new PosTaggerException("Inference failed. Array of tags differs in length from array of tokens.");}
		
		List<TaggedToken<K,G>> ret = new ArrayList<TaggedToken<K,G>>();
		for (int index=0;index<sentenceAsArray.length;++index)
		{
			ret.add(new TaggedToken<K,G>(sentenceAsArray[index], bestTags[index]));
		}
		return ret;

	}

	private final CrfModel<K, G> model;
}
