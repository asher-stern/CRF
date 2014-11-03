package org.postagging.demo;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.postagging.data.TaggedToken;
import org.postagging.data.brown.BrownCorpusReader;

public class DemoReadBrownCorpus
{
	public static final int NUMBER_OF_SENTENCES = 10000;

	public static void main(String[] args)
	{
		try
		{
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
		while (index<NUMBER_OF_SENTENCES && reader.hasNext())
		{
			List<TaggedToken> taggedSentence = reader.next();
			++index;
			for (TaggedToken token : taggedSentence)
			{
				System.out.print(token);
				System.out.print(" ");
				
				tags.add(token.getTag());
			}
			System.out.println();
		}
		
		System.out.println("Tags:");
		for (String tag : tags)
		{
			System.out.println(tag);
		}
		System.out.println("Number of detected tags = "+tags.size());
		
	}
	
	private final String directoryName;
	
	private Set<String> tags = null;

}
