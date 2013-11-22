/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.oval.localization.locale;

import java.util.Locale;

/**
 * @author Sebastian Thomschke
 */
public class ThreadLocalLocaleProvider implements LocaleProvider
{
	private final ThreadLocal<Locale> locale = new ThreadLocal<Locale>();

	/**
	 * Gets the locale of the current thread
	 */
	public Locale getLocale()
	{
		final Locale l = locale.get();
		return l == null ? Locale.getDefault() : l;
	}

	/**
	 * Sets the locale for the current thread
	 */
	public void setLocale(Locale locale)
	{
		this.locale.set(locale);
	}
}
