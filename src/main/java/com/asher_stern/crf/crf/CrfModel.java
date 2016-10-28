package com.asher_stern.crf.crf;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import com.asher_stern.crf.crf.filters.CrfFeaturesAndFilters;

/**
 * This class encapsulates the set of all possible tags, the list of features (f_i), and the list of parameters (\theta_i).
 * 
 * @author Asher Stern
 * Date: Nov 8, 2014
 *
 * @param <K> token type - must implement equals() and hashCode()
 * @param <G> tag type - must implement equals() and hashCode()
 */
public class CrfModel<K,G> implements Serializable // K = token, G = tag
{
	private static final long serialVersionUID = -5703467522848303660L;
	
	public CrfModel(CrfTags<G> crfTags, CrfFeaturesAndFilters<K, G> features, ArrayList<BigDecimal> parameters)
	{
		super();
		this.crfTags = crfTags;
		this.features = features;
		this.parameters = parameters;
	}
	
	
	
	public CrfTags<G> getCrfTags()
	{
		return crfTags;
	}
	public CrfFeaturesAndFilters<K, G> getFeatures()
	{
		return features;
	}
	public ArrayList<BigDecimal> getParameters()
	{
		return parameters;
	}



	private final CrfTags<G> crfTags;
	private final CrfFeaturesAndFilters<K, G> features;
	private final ArrayList<BigDecimal> parameters;
}
