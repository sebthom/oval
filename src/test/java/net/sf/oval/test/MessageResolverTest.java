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

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import java.util.ResourceBundle;

import org.junit.After;
import org.junit.Test;

import net.sf.oval.Validator;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;

/**
 * @author Sebastian Thomschke
 */
public class MessageResolverTest {

   private final Locale defaultLocale = Locale.getDefault();

   @After
   public void tearDown() throws Exception {
      Locale.setDefault(defaultLocale);
   }

   @Test
   public void testMessageResolver() {
      /*
       * test with built-in messages
       */
      Locale.setDefault(Locale.GERMAN);
      assertThat(Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated")).isEqualTo("{context} muss unwahr sein");

      Locale.setDefault(Locale.ENGLISH);
      assertThat(Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated")).isEqualTo("{context} is not false");

      Locale.setDefault(Locale.GERMANY);
      assertThat(Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated")).isEqualTo("{context} muss unwahr sein");

      /*
       * test with custom messages
       */
      Locale.setDefault(defaultLocale);
      final ResourceBundleMessageResolver resolver = (ResourceBundleMessageResolver) Validator.getMessageResolver();
      resolver.addMessageBundle(ResourceBundle.getBundle("net/sf/oval/test/MessageResolverTest", new Locale("", "", "")));
      resolver.addMessageBundle(ResourceBundle.getBundle("net/sf/oval/test/MessageResolverTest", Locale.GERMAN));

      Locale.setDefault(Locale.GERMAN);
      assertThat(Validator.getMessageResolver().getMessage("customCheck.violated")).isEqualTo("FEHLER");

      Locale.setDefault(Locale.ENGLISH);
      assertThat(Validator.getMessageResolver().getMessage("customCheck.violated")).isEqualTo("FAILURE");

      Locale.setDefault(Locale.GERMANY);
      assertThat(Validator.getMessageResolver().getMessage("customCheck.violated")).isEqualTo("FEHLER");
   }
}
