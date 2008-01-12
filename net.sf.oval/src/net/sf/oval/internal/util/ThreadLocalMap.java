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
package net.sf.oval.internal.util;

import java.util.Map;

import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class ThreadLocalMap<K, V> extends ThreadLocal
{
	@SuppressWarnings("unchecked")
	@Override
	public Map<K, V> get()
	{
		return (Map<K, V>) super.get();
	}

	@Override
	public Map<K, V> initialValue()
	{
		return CollectionFactoryHolder.getFactory().createMap();
	}
}
