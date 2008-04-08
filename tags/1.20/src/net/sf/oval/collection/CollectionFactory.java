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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Sebastian Thomschke
 */
public interface CollectionFactory
{
	/**
	 * Instantiate an ArrayList like list object
	 */
	<ValueType> List<ValueType> createList();

	/**
	 * Instantiate an ArrayList like list object
	 */
	<ValueType> List<ValueType> createList(int initialCapacity);

	/**
	 * Instantiate a HashMap like map object
	 */
	<KeyType, ValueType> Map<KeyType, ValueType> createMap();

	/**
	 * Instantiate a HashMap like map object
	 */
	<KeyType, ValueType> Map<KeyType, ValueType> createMap(int initialCapacity);

	/**
	 * Instantiate a HashSet like set object
	 */
	<ValueType> Set<ValueType> createSet();

	/**
	 * Instantiate a HashSet like set object
	 */
	<ValueType> Set<ValueType> createSet(int initialCapacity);
}
