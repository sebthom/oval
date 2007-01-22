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
package net.sf.oval.collections;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javolution.util.FastMap;
import javolution.util.FastSet;
import javolution.util.FastTable;

/**
 * @author Sebastian Thomschke
 */
public class CollectionFactoryJavalutionImpl extends CollectionFactory
{
	public final static CollectionFactoryJavalutionImpl INSTANCE = new CollectionFactoryJavalutionImpl();

	protected CollectionFactoryJavalutionImpl()
	{}

	/**
	 * If Javalution is in the classpath this function returns a new <code>javolution.util.FastTable</code> 
	 * otherwise a new <code>java.util.ArrayList</code> is returned. 
	 */
	public <ItemType> List<ItemType> createList()
	{
		return new FastTable<ItemType>();
	}

	/**
	 * If Javalution is in the classpath this function returns a new <code>javolution.util.FastTable</code> 
	 * otherwise a new <code>java.util.ArrayList</code> is returned. 
	 */
	public <ItemType> List<ItemType> createList(final int initialCapacity)
	{
		return new FastTable<ItemType>(initialCapacity);
	}

	/**
	 * If Javalution is in the classpath this function returns a new <code>javolution.util.FastMap</code> 
	 * otherwise a new <code>java.util.HashMap</code> is returned. 
	 */
	public <KeyType, ValueType> Map<KeyType, ValueType> createMap()
	{
		return new FastMap<KeyType, ValueType>();
	}

	/**
	 * If Javalution is in the classpath this function returns a new <code>javolution.util.FastMap</code> 
	 * otherwise a new <code>java.util.HashMap</code> is returned. 
	 */
	public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity)
	{
		return new FastMap<KeyType, ValueType>(initialCapacity);
	}

	/**
	 * If Javalution is in the classpath this function returns a new <code>javolution.util.FastSet</code> 
	 * otherwise a new <code>java.util.HashSet</code> is returned. 
	 */
	public <ItemType> Set<ItemType> createSet()
	{
		return new FastSet<ItemType>();
	}

	/**
	 * If Javalution is in the classpath this function returns a new <code>javolution.util.FastSet</code> 
	 * otherwise a new <code>java.util.HashSet</code> is returned. 
	 */
	public <ItemType> Set<ItemType> createSet(final int initialCapacity)
	{
		return new FastSet<ItemType>(initialCapacity);
	}
}
