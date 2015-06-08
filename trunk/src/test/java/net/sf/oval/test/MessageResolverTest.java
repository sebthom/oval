/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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
package net.sf.oval.test;

import java.util.Locale;
import java.util.ResourceBundle;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.localization.message.ResourceBundleMessageResolver;

public class MessageResolverTest extends TestCase
{
	public void testMessageResolver()
	{
		/*
		 * test with built-in messages
		 */
		final Locale def = Locale.getDefault();
		/*	Locale.setDefault(Locale.GERMAN);
			assertEquals("{context} muss unwahr sein", Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated"));

			Locale.setDefault(Locale.ENGLISH);
			assertEquals("{context} is not false", Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated"));
		*/
		Locale.setDefault(Locale.GERMANY);
		assertEquals("{context} muss unwahr sein", Validator.getMessageResolver().getMessage("net.sf.oval.constraint.AssertFalse.violated"));

		/*
		 * test with custom messages
		 */
		Locale.setDefault(def);
		final ResourceBundleMessageResolver resolver = (ResourceBundleMessageResolver) Validator.getMessageResolver();
		resolver.addMessageBundle(ResourceBundle.getBundle("net/sf/oval/test/MessageResolverTest",
				ResourceBundleMessageResolver.ROOT_LOCALE));
		resolver.addMessageBundle(ResourceBundle.getBundle("net/sf/oval/test/MessageResolverTest", Locale.GERMAN));

		Locale.setDefault(Locale.GERMAN);
		assertEquals("FEHLER", Validator.getMessageResolver().getMessage("customCheck.violated"));

		Locale.setDefault(Locale.ENGLISH);
		assertEquals("FAILURE", Validator.getMessageResolver().getMessage("customCheck.violated"));

		Locale.setDefault(Locale.GERMANY);
		assertEquals("FEHLER", Validator.getMessageResolver().getMessage("customCheck.violated"));
	}
}
