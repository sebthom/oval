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
package net.sf.oval.internal;

import net.sf.oval.collection.CollectionFactory;
import net.sf.oval.collection.CollectionFactoryJDKImpl;
import net.sf.oval.collection.CollectionFactoryJavalutionImpl;
import net.sf.oval.collection.CollectionFactoryTroveImpl;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * The held factory is used by OVal to instantiate new collections.
 *
 * @author Sebastian Thomschke
 */
public final class CollectionFactoryHolder
{
	private final static Log LOG = Log.getLog(CollectionFactoryHolder.class);

	private static CollectionFactory factory = createDefaultCollectionFactory();

	private static CollectionFactory createDefaultCollectionFactory()
	{
		// if javalution collection classes are found use them by default
		if (ReflectionUtils.isClassPresent("javolution.util.FastMap")
				&& ReflectionUtils.isClassPresent("javolution.util.FastSet")
				&& ReflectionUtils.isClassPresent("javolution.util.FastTable"))
		{
			LOG.info("javolution.util collection classes are available.");

			return new CollectionFactoryJavalutionImpl();
		}
		// else if trove collection classes are found use them by default
		else if (ReflectionUtils.isClassPresent("gnu.trove.THashMap")
				&& ReflectionUtils.isClassPresent("gnu.trove.THashSet"))
		{
			LOG.info("gnu.trove collection classes are available.");

			return new CollectionFactoryTroveImpl();
		}
		// else use JDK collection classes by default
		else
			return new CollectionFactoryJDKImpl();
	}

	/**
	 * Returns a shared instance of the CollectionFactory
	 */
	public static CollectionFactory getFactory()
	{
		return factory;
	}

	/**
	 * 
	 * @param factory the new collection factory to use
	 */
	public static void setFactory(final CollectionFactory factory) throws IllegalArgumentException
	{
		if (factory == null) throw new IllegalArgumentException("factory cannot be null");

		CollectionFactoryHolder.factory = factory;
	}
}
