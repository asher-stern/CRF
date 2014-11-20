package org.postagging.crf;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.postagging.utilities.PosTaggerException;

/**
 * The set of tags which exist in the training corpus, along with maps that indicate which tags follow which tag,
 * and which tags precede which tag in the training corpus.
 * 
 * @author Asher Stern
 * Date: Nov 16, 2014
 *
 * @param <G>
 */
public class CrfTags<G> implements Serializable
{
	private static final long serialVersionUID = -4286815527883493811L;
	
	public CrfTags(Set<G> tags, Map<G, Set<G>> canFollow, Map<G, Set<G>> canPrecede)
	{
		super();
		this.tags = tags;
		this.canFollow = canFollow;
		this.canPrecede = canPrecede;
		
		initCanPrecedeNonNull();
		initPrecedeWhenFirst();
		sanityCheck();
		consistencyCheck();
	}
	
	
	
	
	public Set<G> getTags()
	{
		return tags;
	}
	public Map<G, Set<G>> getCanFollow()
	{
		return canFollow;
	}
	public Map<G, Set<G>> getCanPrecede()
	{
		return canPrecede;
	}
	public Map<G, Set<G>> getCanPrecedeNonNull()
	{
		return canPrecedeNonNull;
	}
	public Map<G, Set<G>> getPrecedeWhenFirst()
	{
		return precedeWhenFirst;
	}




	private void initCanPrecedeNonNull()
	{
		canPrecedeNonNull = new LinkedHashMap<G, Set<G>>();
		for (G tag : canPrecede.keySet())
		{
			Set<G> newSet = new LinkedHashSet<G>();
			for (G precede : canPrecede.get(tag))
			{
				if (precede!=null)
				{
					newSet.add(precede);	
				}
			}
			canPrecedeNonNull.put(tag, newSet);
		}
	}
	
	private void initPrecedeWhenFirst()
	{
		precedeWhenFirst = new LinkedHashMap<G, Set<G>>();
		boolean debug_firstDetected = false;
		for (G tag : canPrecede.keySet())
		{
			if (canPrecede.get(tag).contains(null))
			{
				precedeWhenFirst.put(tag, Collections.singleton(null));
				debug_firstDetected = true;
			}
			else
			{
				precedeWhenFirst.put(tag, Collections.emptySet());
			}
		}
		if (!debug_firstDetected) {throw new PosTaggerException("Error: no tag has null in its can-precede set. This means that no tag can appear as the first tag in a sentence, which is an error.");}
	}


	private void sanityCheck()
	{
		if (!canFollow.keySet().containsAll(tags)) {throw new PosTaggerException("map keys do not contain all tags");}
		if (!canPrecede.keySet().containsAll(tags)) {throw new PosTaggerException("map keys do not contain all tags");}
		if (!canPrecedeNonNull.keySet().containsAll(tags)) {throw new PosTaggerException("map keys do not contain all tags");}
		if (!precedeWhenFirst.keySet().containsAll(tags)) {throw new PosTaggerException("map keys do not contain all tags");}

		if (tags.contains(null)) {throw new PosTaggerException("tags (the set of tags) should not contain null.");}
		
		for (G tag : canPrecedeNonNull.keySet())
		{
			if (canPrecedeNonNull.get(tag).contains(null)) {throw new PosTaggerException("BUG");}
		}
		
		for (G tag : canFollow.keySet())
		{
			if (canFollow.get(tag).contains(null))
			{
				throw new PosTaggerException("Error: null appears as a tag that can follow a given tag, which is an error, since null is a virtual tag for the virtual token the precedes the first token.");
			}
		}
		if (!(canFollow.containsKey(null)))
		{
			throw new PosTaggerException("Error: canFollow does not contain null key. This means that it is not specified which tags can be assigned to a first token of a sentence.");
		}
	}
	
	private void consistencyCheck()
	{
		for (G tag : canFollow.keySet())
		{
			for (G follow : canFollow.get(tag))
			{
				if (!(canPrecede.get(follow).contains(tag)))
				{
					throw new PosTaggerException("can-follow and can-preced are inconsistent.");
				}
			}
		}
		
		for (G tag : canPrecede.keySet())
		{
			for (G precede : canPrecede.get(tag))
			{
				if (!canFollow.get(precede).contains(tag))
				{
					throw new PosTaggerException("can-follow and can-preced are inconsistent.");
				}
			}
		}
	}


	private final Set<G> tags;
	private final Map<G, Set<G>> canFollow;
	private final Map<G, Set<G>> canPrecede;
	private Map<G, Set<G>> canPrecedeNonNull; // like canPrecede, but none of the sets contains null.
	private Map<G, Set<G>> precedeWhenFirst; // What can precede the tag when it is the first token: it might be null, or nothing (if the tags has never been encountered as the first tag in a sentence)
}
