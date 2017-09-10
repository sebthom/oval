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

import java.util.Enumeration;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.Set;

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

    private static class BundlesAndKeys {
        private final Map<Locale, Set<ResourceBundle>> bundlesOfLocales = getCollectionFactory().createMap(8);
        private final Map<ResourceBundle, Set<String>> keysOfBundles = getCollectionFactory().createMap(8);

        @Override
        public BundlesAndKeys clone() {
            final BundlesAndKeys clone = new BundlesAndKeys();
            for (final Entry<Locale, Set<ResourceBundle>> entry : bundlesOfLocales.entrySet()) {
                final Set<ResourceBundle> keys = getCollectionFactory().createSet();
                keys.addAll(entry.getValue());
                clone.bundlesOfLocales.put(entry.getKey(), keys);
            }
            clone.keysOfBundles.putAll(keysOfBundles);
            return clone;
        }
    }

    private static final Log LOG = Log.getLog(ResourceBundleMessageResolver.class);

    public static final ResourceBundleMessageResolver INSTANCE = new ResourceBundleMessageResolver();

    protected static final Locale ROOT_LOCALE = new Locale("", "", "");

    private BundlesAndKeys bundlesAndKeys = new BundlesAndKeys();
    private final Object writeLock = new Object();

    protected void addMessageBundle(final BundlesAndKeys context, final ResourceBundle bundle, final Locale locale) {
        Set<ResourceBundle> bundlesOfLocale = context.bundlesOfLocales.get(locale);
        if (bundlesOfLocale == null) {
            bundlesOfLocale = getCollectionFactory().createSet();
            context.bundlesOfLocales.put(locale, bundlesOfLocale);
        }

        if (bundlesOfLocale.contains(bundle))
            return;

        bundlesOfLocale.add(bundle);
        final Set<String> keys = getCollectionFactory().createSet();
        for (final Enumeration<String> keysEnum = bundle.getKeys(); keysEnum.hasMoreElements();) {
            keys.add(keysEnum.nextElement());
        }
        context.keysOfBundles.put(bundle, keys);
    }

    /**
     * Adds a message bundle
     *
     * @return true if the bundle was registered and false if it was already registered
     */
    public boolean addMessageBundle(final ResourceBundle bundle) {
        Assert.argumentNotNull("bundle", bundle);

        return addMessageBundle(bundle, bundle.getLocale());
    }

    protected boolean addMessageBundle(final ResourceBundle bundle, final Locale locale) {
        synchronized (writeLock) {
            // check if bundle is already registered for this locale
            Set<ResourceBundle> bundlesOfLocale = bundlesAndKeys.bundlesOfLocales.get(locale);
            if (bundlesOfLocale != null && bundlesOfLocale.contains(bundle))
                return false;

            final BundlesAndKeys copy = bundlesAndKeys.clone();
            bundlesOfLocale = copy.bundlesOfLocales.get(locale);
            if (bundlesOfLocale == null) {
                bundlesOfLocale = getCollectionFactory().createSet();
                copy.bundlesOfLocales.put(locale, bundlesOfLocale);

                // add the message bundle for the pre-built constraints
                try {
                    addMessageBundle(copy, ResourceBundle.getBundle("net/sf/oval/Messages", locale), locale);
                } catch (final MissingResourceException ex) {
                    LOG.debug("No message bundle net.sf.oval.Messages for locale [%s] found.", ex, locale);
                }
            }
            addMessageBundle(copy, bundle, locale);

            bundlesAndKeys = copy;
        }
        return true;
    }

    public String getMessage(final String key) {
        final Locale currentLocale = Validator.getLocaleProvider().getLocale();
        final String msg = getMessage(key, currentLocale);
        if (msg != null)
            return msg;

        final Locale defaultLocale = Locale.getDefault();
        if (!currentLocale.equals(defaultLocale))
            return getMessage(key, defaultLocale);

        return null;
    }

    protected String getMessage(final String key, final Locale locale) {
        BundlesAndKeys context = bundlesAndKeys;
        Set<ResourceBundle> bundlesOfLocale = context.bundlesOfLocales.get(locale);
        if (bundlesOfLocale == null) {
            synchronized (writeLock) {
                bundlesOfLocale = bundlesAndKeys.bundlesOfLocales.get(locale);
                if (bundlesOfLocale == null) {
                    final BundlesAndKeys copy = bundlesAndKeys.clone();
                    bundlesOfLocale = getCollectionFactory().createSet();
                    copy.bundlesOfLocales.put(locale, bundlesOfLocale);

                    // add the message bundle for the pre-built constraints
                    try {
                        addMessageBundle(copy, ResourceBundle.getBundle("net/sf/oval/Messages", locale), locale);
                    } catch (final MissingResourceException ex) {
                        LOG.debug("No message bundle net.sf.oval.Messages for locale [%s] found.", ex, locale);
                    }

                    bundlesAndKeys = copy;
                }
                context = bundlesAndKeys;
            }
        }

        for (final ResourceBundle bundle : bundlesOfLocale) {
            final Set<String> keys = context.keysOfBundles.get(bundle);
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

    /**
     * Removes the message bundle
     *
     * @return true if the bundle was registered and false if it wasn't registered
     */
    public boolean removeMessageBundle(final ResourceBundle bundle) {
        Assert.argumentNotNull("bundle", bundle);

        final Locale bundleLocale = bundle.getLocale();

        synchronized (writeLock) {
            // check if bundle is registered for this locale
            final Set<ResourceBundle> bundlesOfLocale = bundlesAndKeys.bundlesOfLocales.get(bundleLocale);
            if (bundlesOfLocale == null || !bundlesOfLocale.contains(bundle))
                return false;

            final BundlesAndKeys copy = bundlesAndKeys.clone();
            copy.bundlesOfLocales.get(bundleLocale).remove(bundle);
            copy.keysOfBundles.remove(bundle);

            bundlesAndKeys = copy;
        }
        return true;
    }
}
