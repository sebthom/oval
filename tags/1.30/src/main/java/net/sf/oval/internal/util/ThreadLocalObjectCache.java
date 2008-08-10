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
package net.sf.oval.internal.util;

/**
 * @author Sebastian Thomschke
 */
public class ThreadLocalObjectCache<K, V> extends ThreadLocal<ObjectCache<K, V>>
{
	private final int maxElementsToKeep;

	public ThreadLocalObjectCache()
	{
		this.maxElementsToKeep = -1;
	}

	public ThreadLocalObjectCache(final int maxElementsToKeep)
	{
		this.maxElementsToKeep = maxElementsToKeep;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObjectCache<K, V> initialValue()
	{
		return new ObjectCache<K, V>(maxElementsToKeep);
	}
}
