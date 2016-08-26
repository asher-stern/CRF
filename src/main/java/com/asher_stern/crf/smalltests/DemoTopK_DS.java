package com.asher_stern.crf.smalltests;

import java.util.List;

import com.asher_stern.crf.utilities.TopK_DateStructure;

/**
 * 
 * @author Asher Stern
 * Date: Nov 19, 2014
 *
 */
public class DemoTopK_DS
{

	public static void main(String[] args)
	{
		try
		{
			new DemoTopK_DS().go();
		}
		catch(Throwable t)
		{
			t.printStackTrace(System.out);
		}
	}
	
	public void go()
	{
		int k = 3;
		TopK_DateStructure<Integer> ds = new TopK_DateStructure<Integer>(k);
		ds.insert(5);
		ds.insert(4);
		ds.insert(6);
		ds.insert(2);
		ds.insert(9);
		ds.insert(3);
		ds.insert(7);
		ds.insert(4);
		ds.insert(5);
		ds.insert(5);
		ds.insert(6);
		ds.insert(6);
		ds.insert(1);
		ds.insert(0);
		ds.insert(-1);
		ds.insert(7);
		ds.insert(8);
		ds.insert(4);
		ds.insert(6);
		ds.insert(11);
		ds.insert(4);
		ds.insert(6);
		ds.insert(2);
		ds.insert(9);
		ds.insert(3);
		ds.insert(7);
		ds.insert(4);
		ds.insert(5);
		ds.insert(5);
		ds.insert(6);
		ds.insert(6);
		ds.insert(1);
		ds.insert(0);
		ds.insert(-1);
		ds.insert(7);
		ds.insert(8);
		ds.insert(4);
		ds.insert(6);
		ds.insert(5);
		ds.insert(5);
		
		List<Integer> list = ds.getTopK();
		for (int i : list)
		{
			System.out.println(i);
		}
	}

}
