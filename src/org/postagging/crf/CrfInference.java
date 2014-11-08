package org.postagging.crf;

import java.util.List;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K>
 * @param <G>
 */
public interface CrfInference<K,G>
{
	public List<G> inferBestTagSequence(CrfModel<K, G> model, List<CrfTaggedToken<K, G>> sentence);
}
