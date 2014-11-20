package org.postagging.demo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.postagging.data.StringTaggedToken;
import org.postagging.postaggers.crf.CrfPosTagger;
import org.postagging.postaggers.crf.CrfPosTaggerLoader;
import org.postagging.utilities.ExceptionUtil;
import org.postagging.utilities.log4j.Log4jInit;
import org.postagging.utilities.PosTaggerException;


/**
 * Run the CrfPosTagger on a given text file.
 * This program loads the CRF pos tagging model from the given directory, processes the given file, and write the tagged
 * sentences into the output file.
 * The input file should contain a single <B>tokenized</B> sentence in each line.
 * 
 * @author Asher Stern
 * Date: Nov 20, 2014
 *
 */
public class UsePosTagger
{

	public static void main(String[] args)
	{
		try
		{
			Log4jInit.init(Level.DEBUG);
			try
			{
				int index=0;
				UsePosTagger application = new UsePosTagger(new File(args[index++]),new File(args[index++]),new File(args[index++]));
				application.go();
			}
			catch(Throwable t)
			{
				ExceptionUtil.logException(t, logger);
			}
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}

	public UsePosTagger(File crfPosTaggerModelDirectory, File sentencesToTag, File outputFile)
	{
		super();
		this.crfPosTaggerModelDirectory = crfPosTaggerModelDirectory;
		this.sentencesToTag = sentencesToTag;
		this.outputFile = outputFile;
	}
	
	public void go()
	{
		logger.info("Loading pos tagger ...");
		CrfPosTaggerLoader loader = new CrfPosTaggerLoader();
		CrfPosTagger posTagger = loader.load(crfPosTaggerModelDirectory);
		logger.info("Loading pos tagger - done.");
		logger.info("Processing ...");
		try(BufferedReader reader = new BufferedReader(new FileReader(sentencesToTag)))
		{
			try(PrintWriter writer = new PrintWriter(outputFile))
			{
				String line = reader.readLine();
				while (line!=null)
				{
					line = line.trim();
					if (line.length()<=0)
					{
						break;
					}
					
					String[] tokens = line.split("\\s+");
					ArrayList<String> listTokens = new ArrayList<String>(tokens.length);
					for (String token : tokens) {listTokens.add(token);}
					List<StringTaggedToken> taggedTokens = posTagger.tagSentence(listTokens);
					writer.println(representTaggedTokens(taggedTokens));
					
					line = reader.readLine();
				}
			}
		}
		catch (IOException e)
		{
			throw new PosTaggerException("IO failuer.",e);
		}
		logger.info("Processing - done.");
	}
	
	
	private String representTaggedTokens(List<StringTaggedToken> taggedTokens)
	{
		StringBuilder sb = new StringBuilder();
		boolean firstIteration = true;
		for (StringTaggedToken taggedToken : taggedTokens)
		{
			if (firstIteration) {firstIteration=false;}
			else {sb.append(" ");}
			sb.append(taggedToken.getToken()).append("/").append(taggedToken.getTag());
		}
		return sb.toString();
	}

	
	
	
	




	private final File crfPosTaggerModelDirectory;
	private final File sentencesToTag;
	private final File outputFile;

	private static final Logger logger = Logger.getLogger(UsePosTagger.class);
}
