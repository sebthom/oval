/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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

   protected static class BundlesAndKeys {
      private final Map<Locale, Set<ResourceBundle>> bundlesOfLocales;
      private final Map<ResourceBundle, Set<String>> keysOfBundles;

      public BundlesAndKeys() {
         bundlesOfLocales = getCollectionFactory().createMap(8);
         keysOfBundles = getCollectionFactory().createMap(8);
      }

      public BundlesAndKeys(final BundlesAndKeys copyFrom) {
         bundlesOfLocales = getCollectionFactory().createMap(copyFrom.bundlesOfLocales.size());
         for (final Entry<Locale, Set<ResourceBundle>> entry : copyFrom.bundlesOfLocales.entrySet()) {
            bundlesOfLocales.put(entry.getKey(), getCollectionFactory().createSet(entry.getValue()));
         }

         keysOfBundles = getCollectionFactory().createMap(copyFrom.keysOfBundles.size());
         keysOfBundles.putAll(copyFrom.keysOfBundles);
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

         final BundlesAndKeys copy = new BundlesAndKeys(bundlesAndKeys);
         bundlesOfLocale = copy.bundlesOfLocales.get(locale);
         if (bundlesOfLocale == null) {
            bundlesOfLocale = getCollectionFactory().createSet();
            copy.bundlesOfLocales.put(locale, bundlesOfLocale);

            // add the message bundle for the pre-built constraints
            try {
               addMessageBundle(copy, ResourceBundle.getBundle("net/sf/oval/Messages", locale), locale);
            } catch (final MissingResourceException ex) {
               LOG.debug("No message bundle net.sf.oval.Messages for locale [{1}] found.", ex, locale);
            }
         }
         addMessageBundle(copy, bundle, locale);

         bundlesAndKeys = copy;
      }
      return true;
   }

   @Override
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
               final BundlesAndKeys copy = new BundlesAndKeys(bundlesAndKeys);
               bundlesOfLocale = getCollectionFactory().createSet();
               copy.bundlesOfLocales.put(locale, bundlesOfLocale);

               // add the message bundle for the pre-built constraints
               try {
                  addMessageBundle(copy, ResourceBundle.getBundle("net/sf/oval/Messages", locale), locale);
               } catch (final MissingResourceException ex) {
                  LOG.debug("No message bundle net.sf.oval.Messages for locale [{1}] found.", ex, locale);
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

         final BundlesAndKeys copy = new BundlesAndKeys(bundlesAndKeys);
         copy.bundlesOfLocales.get(bundleLocale).remove(bundle);
         copy.keysOfBundles.remove(bundle);

         bundlesAndKeys = copy;
      }
      return true;
   }
}
