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
package net.sf.oval.internal.util;

import java.lang.reflect.Method;
import java.util.Collection;

/**
 * @author Sebastian Thomschke
 */
public final class ArrayUtils
{
	public static final Object[] EMPTY_CLASS_ARRAY = new Class[0];
	public static final Method[] EMPTY_METHOD_ARRAY = new Method[0];
	public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
	public static final String[] EMPTY_STRING_ARRAY = new String[0];

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

	public static <T> boolean containsSame(final T[] theArray, final T theItem)
	{
		for (final T t : theArray)
		{
			if (t == theItem) return true;
		}
		return false;
	}

	public static <T> boolean containsEqual(final T[] theArray, final T theItem)
	{
		for (final T t : theArray)
		{
			if (t == theItem) return true;
			if (t != null && t.equals(theItem)) return true;
		}
		return false;
	}

	private ArrayUtils()
	{
	// do nothing
	}
}
