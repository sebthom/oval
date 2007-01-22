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
package net.sf.oval.utils;

/**
 * @author Sebastian Thomschke
 */
public final class StringUtils
{
	public static String implode(final Object[] values, final String delimiter)
	{
		final StringBuilder out = new StringBuilder();

		for (int i = 0; i < values.length; i++)
		{
			if (i > 0) out.append(delimiter);
			out.append(values[i]);
		}
		return out.toString();
	}

	public static String replaceAll(final String searchIn, final String searchFor, final String replaceWith)
	{
		final StringBuilder out = new StringBuilder();

		int startSearchAt = 0, foundAt = 0, searchForLength = searchFor.length();

		while ((foundAt = searchIn.indexOf(searchFor, startSearchAt)) >= 0)
		{
			out.append(searchIn.substring(startSearchAt, foundAt)).append(replaceWith);
			startSearchAt = foundAt + searchForLength;
		}

		return out.append(searchIn.substring(startSearchAt, searchIn.length())).toString();
	}

	/**
	 * private constructor
	 */
	private StringUtils()
	{
		// do nothing
	}
}