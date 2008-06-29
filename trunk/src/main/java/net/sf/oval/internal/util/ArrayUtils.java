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

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Sebastian Thomschke
 */
public final class ArrayUtils
{
	public final static Object[] EMPTY_CLASS_ARRAY = new Class[0];
	public final static Method[] EMPTY_METHOD_ARRAY = new Method[0];
	public final static Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public final static String[] EMPTY_STRING_ARRAY = new String[0];

	/**
	 * @throws IllegalArgumentException if <code>collection == null</code>
	 */
	public static <T> int addAll(final Collection<T> collection, final T... elements) throws IllegalArgumentException
	{
		Assert.notNull("collection", collection);

		if (elements == null) return 0;

		int count = 0;
		for (final T elem : elements)
		{
			if (collection.add(elem))
			{
				count++;
			}
		}
		return count;
	}

	private ArrayUtils()
	{
	// do nothing
	}
}
