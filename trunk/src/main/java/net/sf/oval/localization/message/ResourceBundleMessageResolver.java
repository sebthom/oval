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
package net.sf.oval.localization.message;

import static net.sf.oval.Validator.*;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import net.sf.oval.Validator;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Assert;

/**
 * Default implementation that resolves messages based
 * on the registered resource bundles.
 *
 * @author Sebastian Thomschke
 */
public class ResourceBundleMessageResolver implements MessageResolver
{
	private static final Log LOG = Log.getLog(ResourceBundleMessageResolver.class);

	public static final ResourceBundleMessageResolver INSTANCE = new ResourceBundleMessageResolver();

	private final Map<ResourceBundle, List<String>> messageBundleKeys = getCollectionFactory().createMap(8);
	private final Map<Locale, ArrayList<ResourceBundle>> messageBundlesByLocale = getCollectionFactory().createMap(8);

	/**
	 * Adds a message bundle
	 *
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it was already registered
	 */
	public boolean addMessageBundle(final ResourceBundle messageBundle)
	{
		final ArrayList<ResourceBundle> messageBundles = getMessageBundlesForLocale(messageBundle.getLocale());

		if (messageBundles.contains(messageBundle)) return false;

		messageBundles.add(0, messageBundle);
		final List<String> keys = getCollectionFactory().createList();

		for (final Enumeration<String> keysEnum = messageBundle.getKeys(); keysEnum.hasMoreElements();)
			keys.add(keysEnum.nextElement());

		messageBundleKeys.put(messageBundle, keys);

		return true;
	}

	public String getMessage(final String key)
	{
		final List<ResourceBundle> messageBundles = getMessageBundlesForLocale(Validator.getLocaleProvider().getLocale());

		for (final ResourceBundle bundle : messageBundles)
		{
			final List<String> keys = messageBundleKeys.get(bundle);
			if (keys.contains(key)) return bundle.getString(key);
		}
		return null;
	}

	private ArrayList<ResourceBundle> getMessageBundlesForLocale(Locale locale)
	{
		Assert.argumentNotNull("locale", locale);

		ArrayList<ResourceBundle> mbs = messageBundlesByLocale.get(locale);
		if (mbs == null)
		{
			mbs = new ArrayList<ResourceBundle>();
			messageBundlesByLocale.put(locale, mbs);
			try
			{
				// add the message bundle for the pre-built constraints
				mbs.add(ResourceBundle.getBundle("net/sf/oval/Messages", locale));
			}
			catch (final MissingResourceException ex)
			{
				LOG.debug("No message bundle net.sf.oval.Messages for locale %s found.", ex, locale);
			}
		}
		return mbs;
	}

	/**
	 * Removes the message bundle
	 *
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it wasn't registered
	 */
	public boolean removeMessageBundle(final ResourceBundle messageBundle)
	{
		final List<ResourceBundle> messageBundles = getMessageBundlesForLocale(messageBundle.getLocale());

		if (!messageBundles.contains(messageBundle)) return false;

		messageBundles.remove(messageBundle);
		messageBundleKeys.remove(messageBundle);
		return true;
	}
}
