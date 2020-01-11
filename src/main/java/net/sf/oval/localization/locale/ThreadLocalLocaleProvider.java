/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
