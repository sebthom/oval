/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
	public <ValueType> List<ValueType> createList()
	{
		return new ArrayList<ValueType>();
	}

	public <ValueType> List<ValueType> createList(final int initialCapacity)
	{
		return new ArrayList<ValueType>(initialCapacity);
	}

	public <KeyType, ValueType> Map<KeyType, ValueType> createMap()
	{
		return new LinkedHashMap<KeyType, ValueType>();
	}

	public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity)
	{
		return new LinkedHashMap<KeyType, ValueType>(initialCapacity);
	}

	public <ValueType> Set<ValueType> createSet()
	{
		return new LinkedHashSet<ValueType>();
	}

	public <ValueType> Set<ValueType> createSet(final int initialCapacity)
	{
		return new LinkedHashSet<ValueType>(initialCapacity);
	}
}
