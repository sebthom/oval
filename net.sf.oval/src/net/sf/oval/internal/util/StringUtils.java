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

import java.util.Collection;

/**
 * @author Sebastian Thomschke
 */
public final class StringUtils
{
	public static String implode(final Collection< ? > values, final String delimiter)
	{
		return implode(values.toArray(), delimiter);
	}

	public static String implode(final Object[] values, final String delimiter)
	{
		if (values == null) return "";

		final StringBuilder out = new StringBuilder();

		for (int i = 0, l = values.length; i < l; i++)
		{
			if (i > 0) out.append(delimiter);
			out.append(values[i]);
		}
		return out.toString();
	}

	/**
	 * high-performance case-sensitive string replacement
	 */
	public static String replaceAll(final String searchIn, final String searchFor,
			final String replaceWith)
	{
		final StringBuilder out = new StringBuilder();

		int searchFrom = 0, foundAt = 0;
		final int searchForLength = searchFor.length();

		while ((foundAt = searchIn.indexOf(searchFor, searchFrom)) >= 0)
		{
			out.append(searchIn.substring(searchFrom, foundAt)).append(replaceWith);
			searchFrom = foundAt + searchForLength;
		}

		return out.append(searchIn.substring(searchFrom, searchIn.length())).toString();
	}

	/**
	 * private constructor
	 */
	private StringUtils()
	{
	// do nothing
	}
}