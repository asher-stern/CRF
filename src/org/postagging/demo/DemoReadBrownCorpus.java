package org.postagging.demo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.postagging.data.TaggedToken;
import org.postagging.data.brown.BrownCorpusReader;
import org.postagging.utilities.log4j.Log4jInit;

/**
 * 
 * @author Asher Stern
 * Date: Nov 3, 2014
 *
 */
public class DemoReadBrownCorpus
{
	public static final int NUMBER_OF_SENTENCES = 10000;

	public static void main(String[] args)
	{
		try
		{
			Log4jInit.init();
			new DemoReadBrownCorpus(args[0]).go();
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}

	}
	
	
	
	public DemoReadBrownCorpus(String directoryName)
	{
		super();
		this.directoryName = directoryName;
	}



	public void go()
	{
		tags = new LinkedHashSet<String>();
		BrownCorpusReader reader = new BrownCorpusReader(directoryName);
		int index=0;
		//while (index<NUMBER_OF_SENTENCES && reader.hasNext())
		while (reader.hasNext())
		{
			List<TaggedToken> taggedSentence = reader.next();
			++index;
//			StringBuilder sb = new StringBuilder();
			for (TaggedToken token : taggedSentence)
			{
//				sb.append(token).append(" ");
				tags.add(token.getTag());
			}
//			logger.info(sb.toString());
			if (0==index%10000)
			{
				logger.info(index);
			}
		}
		
		logger.info("Tags:");
		for (String tag : tags)
		{
			logger.info(tag);
		}
		logger.info("Number of detected tags = "+tags.size());
		logger.info("Number of sentences = "+index);
		
	}
	
	private final String directoryName;
	
	private Set<String> tags = null;
	
	private static final Logger logger = Logger.getLogger(DemoReadBrownCorpus.class);

}
