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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

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

	public static List<Object> arrayToList(Object array)
	{
		Assert.notNull("array", array);

		if (array instanceof Object[]) return Arrays.asList((Object[]) array);
		if (array instanceof byte[])
		{
			byte[] arrayCasted = (byte[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (byte i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof char[])
		{
			char[] arrayCasted = (char[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (char i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof short[])
		{
			short[] arrayCasted = (short[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (short i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof int[])
		{
			int[] arrayCasted = (int[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (int i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof long[])
		{
			long[] arrayCasted = (long[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (long i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof double[])
		{
			double[] arrayCasted = (double[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (double i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof float[])
		{
			float[] arrayCasted = (float[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (float i : arrayCasted)
				result.add(i);
			return result;
		}
		if (array instanceof boolean[])
		{
			boolean[] arrayCasted = (boolean[]) array;
			List<Object> result = new ArrayList<Object>(arrayCasted.length);
			for (boolean i : arrayCasted)
				result.add(i);
			return result;
		}

		throw new IllegalArgumentException("Parameter [array] must be an array");
	}

	private ArrayUtils()
	{
	// do nothing
	}
}
