package org.postagging.utilities;

/**
 * 
 * @author Asher Stern
 * Date: Nov 13, 2014
 *
 * @param <K> cannot be null
 * @param <V> cannot be null
 */
public class LastCache<K,V>
{
	public synchronized void put(K key, V value)
	{
		if (null==key) {throw new PosTaggerException("null key");}
		if (null==value) {throw new PosTaggerException("null value");}
		
		this.key = key;
		this.value = value;
	}
	
	public synchronized V get(K key)
	{
		if (null==key) {throw new PosTaggerException("null key");}
		else if (key.equals(this.key))
		{
			return value;
		}
		else
		{
			return null;
		}
	}

	private K key = null;
	private V value = null;
}
