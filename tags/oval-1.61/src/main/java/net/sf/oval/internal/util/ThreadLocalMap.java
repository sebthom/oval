/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Map;

/**
 * @author Sebastian Thomschke
 */
public final class ThreadLocalMap<K, V> extends ThreadLocal<Map<K, V>>
{
	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<K, V> initialValue()
	{
		return getCollectionFactory().createMap();
	}
}
