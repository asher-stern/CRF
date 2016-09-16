package com.asher_stern.crf.postagging.data.penn;

import java.io.File;
import java.util.List;

import com.asher_stern.crf.utilities.TaggedToken;

/**
 * Iterable implementation for Penn Tree-Bank corpus, where each iterated item is a pos-tagged sentence.
 * The input for this class is a directory in the file-system, which has no sub-directories, and contains
 * ".mrg" files.
 * Each ".mrg" file contains parse trees. For example:
 * <pre>
 * ( (S 
 *  (NP-SBJ 
 *    (NP (NNP Pierre) (NNP Vinken) )
 *    (, ,) 
 *    (ADJP 
 *      (NP (CD 61) (NNS years) )
 *      (JJ old) )
 *    (, ,) )
 *  (VP (MD will) 
 *    (VP (VB join) 
 *      (NP (DT the) (NN board) )
 *      (PP-CLR (IN as) 
 *        (NP (DT a) (JJ nonexecutive) (NN director) ))
 *      (NP-TMP (NNP Nov.) (CD 29) )))
 *  (. .) ))

 * </pre>
 * 
 * <p>
 * Obtaining the full Penn Tree-Bank is by purchasing it from LDC.
 * However, a free sample of 5-10% of the full Tree-Bank is available for free at
 * <a href="https://raw.githubusercontent.com/nltk/nltk_data/gh-pages/packages/corpora/treebank.zip">https://raw.githubusercontent.com/nltk/nltk_data/gh-pages/packages/corpora/treebank.zip</a>.
 * This data is for non-commercial use only. 
 * 
 * 
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 */
public class PennCorpus implements Iterable<List<TaggedToken<String, String>>>
{
	
	// note for myself Size of corpus = 49208 sentences
	
	
	/**
	 * Constructs the Iterable with the Penn TreeBank directory, as specified in the main comment.
	 * @param directory A directory in the file-system, which contains the files of the Penn TreeBank
	 */
	public PennCorpus(File directory)
	{
		super();
		this.directory = directory;
	}

	@Override
	public PennCorpusReader iterator()
	{
		return new PennCorpusReader(directory);
	}
	
	private final File directory;
}
