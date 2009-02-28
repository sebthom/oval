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
package net.sf.oval.collection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sebastian Thomschke
 */
public class CollectionFactoryJDKImpl implements CollectionFactory
{
	/**
	 * {@inheritDoc}
	 */
	public <ValueType> List<ValueType> createList()
	{
		return new ArrayList<ValueType>();
	}

	/**
	 * {@inheritDoc}
	 */
	public <ValueType> List<ValueType> createList(final int initialCapacity)
	{
		return new ArrayList<ValueType>(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	public <KeyType, ValueType> Map<KeyType, ValueType> createMap()
	{
		return new LinkedHashMap<KeyType, ValueType>();
	}

	/**
	 * {@inheritDoc}
	 */
	public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity)
	{
		return new LinkedHashMap<KeyType, ValueType>(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	public <ValueType> Set<ValueType> createSet()
	{
		return new LinkedHashSet<ValueType>();
	}

	/**
	 * {@inheritDoc}
	 */
	public <ValueType> Set<ValueType> createSet(final int initialCapacity)
	{
		return new LinkedHashSet<ValueType>(initialCapacity);
	}
}
