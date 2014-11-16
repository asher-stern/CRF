package org.postagging.data.penn;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.postagging.utilities.PosTaggerException;

/**
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 */
public class PennFileContentsParser
{
	public PennFileContentsParser(char[] contents)
	{
		super();
		this.contents = contents;
	}
	
	public void parse()
	{
		StringBuilder activeNodeContent = null;
		for (int index=0;index<contents.length;++index)
		{
			char c = contents[index];
			if (Character.isWhitespace(c))
			{
				if (activeNodeContent!=null)
				{
					activeNodeContent.append(c);
				}
			}
			else if (c=='(')
			{
				if (activeNodeContent!=null)
				{
					PennParserTreeNode node = new PennParserTreeNode(activeNodeContent.toString());
					if (!activeStack.isEmpty())
					{
						activeStack.peek().addChild(node);
					}
					activeStack.push(node);
				}
				activeNodeContent = new StringBuilder();
			}
			else if (c==')')
			{
				if (activeNodeContent!=null)
				{
					PennParserTreeNode node = new PennParserTreeNode(activeNodeContent.toString());
					if (!activeStack.isEmpty())
					{
						activeStack.peek().addChild(node);
					}
					activeStack.push(node);
				}
				PennParserTreeNode poped = activeStack.pop();
				if (activeStack.isEmpty())
				{
					trees.add(poped);
				}
				activeNodeContent = null;
			}
			else
			{
				if (activeNodeContent==null) {throw new PosTaggerException("Malformed PTB file contents.");}
				activeNodeContent.append(c);
			}
		}
	}
	
	

	public List<PennParserTreeNode> getTrees()
	{
		return trees;
	}



	private final char[] contents;
	
	private Stack<PennParserTreeNode> activeStack = new Stack<PennParserTreeNode>();
	private List<PennParserTreeNode> trees = new LinkedList<PennParserTreeNode>();
}
