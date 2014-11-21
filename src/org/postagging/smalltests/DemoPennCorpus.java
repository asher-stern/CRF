package org.postagging.smalltests;

import java.io.File;
import java.util.List;

import org.apache.log4j.Level;
import org.postagging.data.penn.PennCorpus;
import org.postagging.utilities.TaggedToken;
import org.postagging.utilities.log4j.Log4jInit;
import org.postagging.data.LimitedSizePosTagCorpusReader;

public class DemoPennCorpus
{

	public static void main(String[] args)
	{
		try
		{
			Log4jInit.init(Level.DEBUG);
			new DemoPennCorpus().go(new File(args[0]));
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}

	}

	public void go(File directory)
	{
		PennCorpus corpus = new PennCorpus(directory);
		LimitedSizePosTagCorpusReader<String,String> reader = new LimitedSizePosTagCorpusReader<String,String>(corpus.iterator(),10);
		while (reader.hasNext())
		{
			List<? extends TaggedToken<String, String>> sentence = reader.next();
			StringBuilder sb = new StringBuilder();
			for (TaggedToken<String, String> taggedToken : sentence)
			{
				sb.append(taggedToken.getToken()).append("/").append( String.format("%-4s", taggedToken.getTag()) ).append(" ");
			}
			System.out.println(sb.toString());
		}
	}
}
