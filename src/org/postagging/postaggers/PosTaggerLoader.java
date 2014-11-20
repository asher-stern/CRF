package org.postagging.postaggers;

import java.io.File;

/**
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public interface PosTaggerLoader
{
	public PosTagger load(File directory);

}
