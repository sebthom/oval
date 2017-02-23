/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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
public class ResourceBundleMessageResolver implements MessageResolver {
    private static final Log LOG = Log.getLog(ResourceBundleMessageResolver.class);

    public static final ResourceBundleMessageResolver INSTANCE = new ResourceBundleMessageResolver();

    public static final Locale ROOT_LOCALE = new Locale("", "", "");

    private final Map<ResourceBundle, List<String>> messageBundleKeys = getCollectionFactory().createMap(8);
    private final Map<Locale, ArrayList<ResourceBundle>> messageBundlesByLocale = getCollectionFactory().createMap(8);

    /**
     * Adds a message bundle
     *
     * @return true if the bundle was registered and false if it was already registered
     */
    public boolean addMessageBundle(final ResourceBundle mb) {
        return addMessageBundle(mb, mb.getLocale());
    }

    protected boolean addMessageBundle(final ResourceBundle bundle, final Locale locale) {
        final ArrayList<ResourceBundle> bundles = getMessageBundlesForLocale(locale);

        if (bundles.contains(bundle))
            return false;

        bundles.add(0, bundle);

        if (!messageBundleKeys.containsKey(bundle)) {
            final List<String> keys = getCollectionFactory().createList();
            for (final Enumeration<String> keysEnum = bundle.getKeys(); keysEnum.hasMoreElements();) {
                keys.add(keysEnum.nextElement());
            }
            messageBundleKeys.put(bundle, keys);
        }

        return true;
    }

    public String getMessage(final String key) {
        final Locale l = Validator.getLocaleProvider().getLocale();
        String msg = getMessage(key, l);
        if (msg == null && !l.equals(Locale.getDefault())) {
            msg = getMessage(key, Locale.getDefault());
        }
        return msg;
    }

    protected String getMessage(final String key, final Locale locale) {
        final List<ResourceBundle> bundles = getMessageBundlesForLocale(locale);

        for (final ResourceBundle bundle : bundles) {
            final List<String> keys = messageBundleKeys.get(bundle);
            if (keys.contains(key))
                return bundle.getString(key);
        }

        // fallback from 'en_US' to 'en' locale
        if (locale.getCountry().length() > 0)
            return getMessage(key, new Locale(locale.getLanguage(), "", ""));
        if (locale.getLanguage().length() > 0)
            return getMessage(key, ROOT_LOCALE);

        return null;
    }

    private ArrayList<ResourceBundle> getMessageBundlesForLocale(final Locale locale) {
        Assert.argumentNotNull("locale", locale);

        ArrayList<ResourceBundle> bundles = messageBundlesByLocale.get(locale);
        if (bundles == null) {
            // Double-check locking to initialize bundles for locale
            synchronized(messageBundlesByLocale) {
                bundles = messageBundlesByLocale.get(locale);
                if (bundles == null) {
                    bundles = new ArrayList<ResourceBundle>();
                    messageBundlesByLocale.put(locale, bundles);
                    try {
                        // add the message bundle for the pre-built constraints
                        addMessageBundle(ResourceBundle.getBundle("net/sf/oval/Messages", locale), locale);
                    } catch (final MissingResourceException ex) {
                        LOG.debug("No message bundle net.sf.oval.Messages for locale %s found.", ex, locale);
                    }
                }
            }
        }
        return bundles;
    }

    /**
     * Removes the message bundle
     *
     * @return true if the bundle was registered and false if it wasn't registered
     */
    public boolean removeMessageBundle(final ResourceBundle bundle) {
        final List<ResourceBundle> bundles = getMessageBundlesForLocale(bundle.getLocale());

        if (!bundles.contains(bundle))
            return false;

        bundles.remove(bundle);
        messageBundleKeys.remove(bundle);
        return true;
    }
}
