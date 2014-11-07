package org.postagging.demo;

import java.util.LinkedList;
import java.util.ListIterator;

public class DemoList
{

	public static void main(String[] args)
	{
		LinkedList<Integer> list = new LinkedList<Integer>();
		list.add(1);
		list.add(2);
		list.add(3);
		
		ListIterator<Integer> iter = list.listIterator(list.size());
		while (iter.hasPrevious())
		{
			Integer i = iter.previous();
			System.out.println(i);
		}

	}

}
