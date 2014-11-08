package org.postagging.crf;

import java.util.ArrayList;
import java.util.Set;

/**
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K> token type - must implement equals() and hashCode()
 * @param <G> tag type - must implement equals() and hashCode()
 */
public class CrfModel<K,G> // K = token, G = tag
{
	public CrfModel(Set<G> tags, ArrayList<CrfFeature<K, G>> features, ArrayList<Double> parameters)
	{
		super();
		this.tags = tags;
		this.features = features;
		this.parameters = parameters;
	}
	
	
	
	public Set<G> getTags()
	{
		return tags;
	}
	public ArrayList<CrfFeature<K, G>> getFeatures()
	{
		return features;
	}
	public ArrayList<Double> getParameters()
	{
		return parameters;
	}



	private final Set<G> tags;
	private final ArrayList<CrfFeature<K, G>> features;
	private final ArrayList<Double> parameters;
}
