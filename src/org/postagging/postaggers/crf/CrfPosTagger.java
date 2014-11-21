package org.postagging.postaggers.crf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.postagging.crf.CrfInference;
import org.postagging.crf.CrfInferenceViterbi;
import org.postagging.crf.CrfModel;
import org.postagging.data.StringTaggedToken;
import org.postagging.postaggers.PosTagger;
import org.postagging.utilities.PosTaggerException;

/**
 * A part-of-speech tagger which assigns the tags using CRF inference. CRF is an acronym of Conditional Random Fields.
 * 
 * @author Asher Stern
 * Date: Nov 10, 2014
 *
 */
public class CrfPosTagger implements PosTagger
{
	public CrfPosTagger(CrfModel<String, String> crfModel)
	{
		super();
		this.crfModel = crfModel;
	}

	@Override
	public List<StringTaggedToken> tagSentence(List<String> sentence)
	{
		if (null==sentence) {return null;}
		if (sentence.size()==0) {return Collections.<StringTaggedToken>emptyList();}
		
		String[] sentenceAsArray = sentence.toArray(new String[0]);
		CrfInference<String, String> crfInference = new CrfInferenceViterbi<String, String>(crfModel, sentenceAsArray);
		String[] bestTags = crfInference.inferBestTagSequence();

		if (sentenceAsArray.length!=bestTags.length) {throw new PosTaggerException("Inference failed. Array of tags differs in length from array of tokens. But in the CrfInference implementation.");}
		
		List<StringTaggedToken> ret = new ArrayList<StringTaggedToken>();
		for (int index=0;index<sentenceAsArray.length;++index)
		{
			ret.add(new StringTaggedToken(sentenceAsArray[index], bestTags[index]));
		}
		return ret;
	}

	private final CrfModel<String, String> crfModel;
}
