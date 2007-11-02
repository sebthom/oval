/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
package net.sf.oval.collection;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastTable;

/**
 * @author Sebastian Thomschke
 */
public class CollectionFactoryJavalutionImpl implements CollectionFactory
{
	public <ItemType> List<ItemType> createList()
	{
		return new FastTable<ItemType>();
	}

	public <ItemType> List<ItemType> createList(final int initialCapacity)
	{
		return new FastTable<ItemType>(initialCapacity);
	}

	public <KeyType, ValueType> Map<KeyType, ValueType> createMap()
	{
		return new FastMap<KeyType, ValueType>();
	}

	public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity)
	{
		return new FastMap<KeyType, ValueType>(initialCapacity);
	}

	public <ItemType> Set<ItemType> createSet()
	{
		return new FastSet<ItemType>();
	}

	public <ItemType> Set<ItemType> createSet(final int initialCapacity)
	{
		return new FastSet<ItemType>(initialCapacity);
	}
}
