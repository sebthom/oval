/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test;

import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;

/**
 * @author Sebastian Thomschke
 */
public class MessageResolverTest extends TestCase {

   private final Locale defaultLocale = Locale.getDefault();

   @Override
   protected void tearDown() throws Exception {
      Locale.setDefault(defaultLocale);
   }

   public void testMessageResolver() {
      /*
       * test with built-in messages
       */
      Locale.setDefault(Locale.GERMAN);
      assertEquals("{context} muss unwahr sein", Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated"));

      Locale.setDefault(Locale.ENGLISH);
      assertEquals("{context} is not false", Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated"));

      Locale.setDefault(Locale.GERMANY);
      assertEquals("{context} muss unwahr sein", Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated"));

      /*
       * test with custom messages
       */
      Locale.setDefault(defaultLocale);
      final ResourceBundleMessageResolver resolver = (ResourceBundleMessageResolver) Validator.getMessageResolver();
      resolver.addMessageBundle(ResourceBundle.getBundle("net/sf/oval/test/MessageResolverTest", new Locale("", "", "")));
      resolver.addMessageBundle(ResourceBundle.getBundle("net/sf/oval/test/MessageResolverTest", Locale.GERMAN));

      Locale.setDefault(Locale.GERMAN);
      assertEquals("FEHLER", Validator.getMessageResolver().getMessage("customCheck.violated"));

      Locale.setDefault(Locale.ENGLISH);
      assertEquals("FAILURE", Validator.getMessageResolver().getMessage("customCheck.violated"));

      Locale.setDefault(Locale.GERMANY);
      assertEquals("FEHLER", Validator.getMessageResolver().getMessage("customCheck.violated"));
   }
}
