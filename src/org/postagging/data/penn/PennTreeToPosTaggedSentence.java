package org.postagging.data.penn;

import java.util.ArrayList;
import java.util.List;

import org.postagging.utilities.PosTaggerException;
import org.postagging.utilities.StringUtilities;
import org.postagging.utilities.TaggedToken;


/**
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 */
public class PennTreeToPosTaggedSentence
{
	public static final String IGNORE_TAG = "-NONE-";
	public static final String PUNCTUATION_TAG = "PUNC";
	public static final int INITIAL_SENTENCE_LENGTH = 30;
	
	
	
	public PennTreeToPosTaggedSentence(PennParserTreeNode tree)
	{
		super();
		this.tree = tree;
	}



	public void extractPosTaggedSentence()
	{
		ArrayList<TaggedToken<String,String>> sentenceArrayList = new ArrayList<TaggedToken<String,String>>(INITIAL_SENTENCE_LENGTH);
		sentence = sentenceArrayList;
		dfsScan(tree);
		sentenceArrayList.trimToSize();
	}
	
	
	
	public List<TaggedToken<String,String>> getSentence()
	{
		return sentence;
	}



	private void dfsScan(PennParserTreeNode node)
	{
		if (node.getChildren().size()==0)
		{
			TaggedToken<String,String> taggedToken = fromLeaf(node.getNodeString());
			if (taggedToken!=null)
			{
				sentence.add(taggedToken);
			}
		}
		for (PennParserTreeNode child : node.getChildren())
		{
			dfsScan(child);
		}
	}
	
	private TaggedToken<String,String> fromLeaf(String leafContents)
	{
		String[] components = leafContents.split("\\s+");
		if (components.length!=2) {throw new PosTaggerException("Malformed leaf in PTB tree.");}
		for (int i=0;i<components.length;++i) {if (components[i]==null) {throw new PosTaggerException("Unexpected null token/tag.");} }
		String tag = components[0].trim();
		if (!(IGNORE_TAG.equals(tag)))
		{
			int indexOfHyphen = tag.indexOf('-');
			if (indexOfHyphen==0)
			{
				tag = PUNCTUATION_TAG;
			}
			else
			{
				if (indexOfHyphen>0)
				{
					tag = tag.substring(0, indexOfHyphen);
				}
				if (!StringUtilities.stringContainsLetter(tag))
				{
					tag = PUNCTUATION_TAG;
				}
			}
			return new TaggedToken<String,String>(components[1].trim(),tag);
		}
		else return null;
	}

	private final PennParserTreeNode tree;
	
	private List<TaggedToken<String,String>> sentence;
}
