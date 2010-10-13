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
package net.sf.oval.localization.message;

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Default implementation that resolves messages based
 * on the registered resource bundles.
 * 
 * @author Sebastian Thomschke
 */
public class ResourceBundleMessageResolver implements MessageResolver
{
	private final Map<ResourceBundle, List<String>> messageBundleKeys = getCollectionFactory().createMap(8);

	private final LinkedList<ResourceBundle> messageBundles = new LinkedList<ResourceBundle>();

	public static final ResourceBundleMessageResolver INSTANCE = new ResourceBundleMessageResolver();

	public ResourceBundleMessageResolver()
	{
		// add the message bundle for the pre-built constraints in the default locale
		addMessageBundle(ResourceBundle.getBundle("net/sf/oval/Messages"));
	}

	/**
	 * Adds a message bundle
	 * 
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it was already registered
	 */
	public final boolean addMessageBundle(final ResourceBundle messageBundle)
	{
		if (messageBundles.contains(messageBundle)) return false;

		messageBundles.addFirst(messageBundle);
		final List<String> keys = getCollectionFactory().createList();

		for (final Enumeration<String> keysEnum = messageBundle.getKeys(); keysEnum.hasMoreElements();)
		{
			keys.add(keysEnum.nextElement());
		}

		messageBundleKeys.put(messageBundle, keys);

		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMessage(final String key)
	{
		for (final ResourceBundle bundle : messageBundles)
		{
			final List<String> keys = messageBundleKeys.get(bundle);
			if (keys.contains(key)) return bundle.getString(key);
		}
		return null;
	}

	/**
	 * Removes the message bundle
	 * 
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it wasn't registered
	 */
	public boolean removeMessageBundle(final ResourceBundle messageBundle)
	{
		if (!messageBundles.contains(messageBundle)) return false;

		messageBundles.remove(messageBundle);
		return true;
	}
}
