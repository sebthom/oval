/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.internal.util;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * @author Sebastian Thomschke
 */
public class ObjectCache<K, V>
{
	private final Map<K, SoftReference<V>> map = new HashMap<K, SoftReference<V>>();

	private final LinkedList<V> objectsLastAccessed = new LinkedList<V>();
	private final int objectsToKeepCount;

	/**
	 * Creates a new cache keeping all objects.
	 */
	public ObjectCache()
	{
		objectsToKeepCount = -1;
	}

	/**
	 * @param maxObjectsToKeep the number of cached objects that should stay in memory when GC 
	 * starts removing SoftReferences to free memory 
	 */
	public ObjectCache(final int maxObjectsToKeep)
	{
		this.objectsToKeepCount = maxObjectsToKeep;
	}

	public void compact()
	{
		for (final Map.Entry<K, SoftReference<V>> entry : map.entrySet())
		{
			final SoftReference<V> ref = entry.getValue();
			if (ref.get() == null)
			{
				map.remove(entry.getKey());
			}
		}
	}

	public boolean contains(final K key)
	{
		return map.containsKey(key);
	}

	public V get(final K key)
	{
		final SoftReference<V> softReference = map.get(key);
		if (softReference != null)
		{
			final V value = softReference.get();

			if (value == null)
			{
				map.remove(key);
			}
			else if (objectsToKeepCount > 0 && value != objectsLastAccessed.getFirst())
			{
				objectsLastAccessed.remove(value);
				objectsLastAccessed.addFirst(value);
				if (objectsLastAccessed.size() > objectsToKeepCount)
				{
					objectsLastAccessed.removeLast();
				}
			}
			return softReference.get();
		}
		return null;
	}

	public void put(final K key, final V value)
	{
		map.remove(key);
		map.put(key, new SoftReference<V>(value));
	}
}