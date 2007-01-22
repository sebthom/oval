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
import java.util.logging.Logger;

/**
 * @author Sebastian Thomschke
 */
public abstract class CollectionFactory
{
	private final static Logger LOG = Logger.getLogger(CollectionFactory.class.getName());

	public static CollectionFactory INSTANCE;

	static
	{
		// if javalution collection classes are found use them by default
		try
		{
			Class.forName("javolution.util.FastMap");
			Class.forName("javolution.util.FastSet");
			Class.forName("javolution.util.FastTable");

			INSTANCE = CollectionFactoryJavalutionImpl.INSTANCE;

			LOG.info("javolution.util collection classes are available.");
		}
		catch (ClassNotFoundException e)
		{
			INSTANCE = CollectionFactoryJDKImpl.INSTANCE;
		}
	}

	/**
	 * Instantiate an ArrayList like list object
	 */
	public abstract <ValueType> List<ValueType> createList();

	/**
	 * Instantiate an ArrayList like list object
	 */
	public abstract <ValueType> List<ValueType> createList(int initialCapacity);

	/**
	 * Instantiate a HashMap like map object
	 */
	public abstract <KeyType, ValueType> Map<KeyType, ValueType> createMap();

	/**
	 * Instantiate a HashMap like map object
	 */
	public abstract <KeyType, ValueType> Map<KeyType, ValueType> createMap(int initialCapacity);

	/**
	 * Instantiate a HashSet like set object
	 */
	public abstract <ValueType> Set<ValueType> createSet();

	/**
	 * Instantiate a HashSet like set object
	 */
	public abstract <ValueType> Set<ValueType> createSet(int initialCapacity);
}
