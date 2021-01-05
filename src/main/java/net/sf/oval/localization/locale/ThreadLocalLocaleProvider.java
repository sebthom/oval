/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.localization.locale;

import java.util.Locale;

/**
 * @author Sebastian Thomschke
 */
public class ThreadLocalLocaleProvider implements LocaleProvider {
   private final ThreadLocal<Locale> locale = new ThreadLocal<>();

   /**
    * Gets the locale of the current thread
    */
   @Override
   public Locale getLocale() {
      final Locale l = locale.get();
      return l == null ? Locale.getDefault() : l;
   }

   /**
    * Sets the locale for the current thread
    */
   public void setLocale(final Locale locale) {
      this.locale.set(locale);
   }
}
