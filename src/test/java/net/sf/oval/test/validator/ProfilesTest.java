/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ProfilesTest extends TestCase
{
	protected static class Person
	{
		@NotNull(/* profiles = { "default" }, */message = "NOTNULL")
		public String city;

		@NotNull(profiles = {"profile1"}, message = "NOTNULL1")
		public String firstName;

		@NotNull(profiles = {"profile2", "profile3"}, message = "NOTNULL2")
		public String lastName;

		@NotNull(profiles = {"profile3", "profile4"}, message = "NOTNULL3")
		public String zipCode;
	}

	public void testAdhocProfiles()
	{
		final Validator validator = new Validator();

		// disable all profiles = no constraints by default
		validator.disableAllProfiles();
		final Person p = new Person();
		List<ConstraintViolation> violations = validator.validate(p, (String[]) null);
		assertEquals(0, violations.size());
		violations = validator.validate(p, "profile1");
		assertEquals(1, violations.size());
		assertEquals("NOTNULL1", violations.get(0).getMessage());
		violations = validator.validate(p, "profile1", "profile2");
		assertEquals(2, violations.size());

		// enable all profiles = all constraints by default
		validator.enableAllProfiles();
		violations = validator.validate(p, (String[]) null);
		assertEquals(4, violations.size());
		violations = validator.validate(p, "profile1");
		assertEquals(1, violations.size());
		assertEquals("NOTNULL1", violations.get(0).getMessage());
		violations = validator.validate(p, "profile1", "profile2");
		assertEquals(2, violations.size());
	}

	public void testProfilesGloballyDisabled()
	{
		final Validator validator = new Validator();

		// disable all profiles = no constraints
		validator.disableAllProfiles();
		assertFalse(validator.isProfileEnabled("profile1"));
		assertFalse(validator.isProfileEnabled("profile2"));
		assertFalse(validator.isProfileEnabled("profile3"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(0, violations.size());
		}

		// enable profile 1
		validator.enableProfile("profile1");
		assertTrue(validator.isProfileEnabled("profile1"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(1, violations.size());
			assertEquals("NOTNULL1", violations.get(0).getMessage());
		}

		// enable profile 1 + 2
		validator.enableProfile("profile2");
		assertTrue(validator.isProfileEnabled("profile2"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(2, violations.size());
		}

		// enable profile 1 + 2 + 3
		validator.enableProfile("profile3");
		assertTrue(validator.isProfileEnabled("profile3"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(3, violations.size());
		}

		// enable profile 1 + 2 + 3 + 4
		assertFalse(validator.isProfileEnabled("profile4"));
		validator.enableProfile("profile4");
		assertTrue(validator.isProfileEnabled("profile4"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(3, violations.size());
		}

		// enable profile 1 + 2 + 3 + 4 + default
		assertFalse(validator.isProfileEnabled("default"));
		validator.enableProfile("default");
		assertTrue(validator.isProfileEnabled("default"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(4, violations.size());
		}
	}

	public void testProfilesGloballyEnabled()
	{
		final Validator validator = new Validator();

		validator.enableAllProfiles();
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(4, violations.size());
		}

		assertTrue(validator.isProfileEnabled("profile1"));
		validator.disableProfile("profile1");
		assertFalse(validator.isProfileEnabled("profile1"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(3, violations.size());
		}

		assertTrue(validator.isProfileEnabled("profile2"));
		validator.disableProfile("profile2");
		assertFalse(validator.isProfileEnabled("profile2"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(3, violations.size());
		}

		assertTrue(validator.isProfileEnabled("profile3"));
		validator.disableProfile("profile3");
		assertFalse(validator.isProfileEnabled("profile3"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(2, violations.size());
			if ("NOTNULL".equals(violations.get(0).getMessage()))
				assertEquals("NOTNULL3", violations.get(1).getMessage());
			else
			{
				assertEquals("NOTNULL3", violations.get(0).getMessage());
				assertEquals("NOTNULL", violations.get(1).getMessage());
			}
		}

		assertTrue(validator.isProfileEnabled("profile4"));
		validator.disableProfile("profile4");
		assertFalse(validator.isProfileEnabled("profile4"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(1, violations.size());
		}

		assertTrue(validator.isProfileEnabled("default"));
		validator.disableProfile("default");
		assertFalse(validator.isProfileEnabled("default"));
		{
			final Person p = new Person();
			final List<ConstraintViolation> violations = validator.validate(p);
			assertEquals(0, violations.size());
		}
	}
}
