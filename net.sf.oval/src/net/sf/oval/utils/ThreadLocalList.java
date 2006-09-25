/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.utils;

import java.util.List;

import net.sf.oval.collections.CollectionFactory;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.0 $
 */
public class ThreadLocalList<T> extends ThreadLocal
{
	public List<T> initialValue()
	{
		return CollectionFactory.INSTANCE.createList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> get()
	{
		return (List<T>) super.get();
	}
}
