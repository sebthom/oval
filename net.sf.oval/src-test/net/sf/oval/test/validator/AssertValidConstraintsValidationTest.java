/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertValid;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class AssertValidConstraintsValidationTest extends TestCase
{
	private static class Person
	{
		@NotNull
		public String firstName;

		@NotNull
		public String lastName;

		@AssertValid(message = "ASSERT_VALID")
		public Address homeAddress;

		@AssertValid(message = "ASSERT_VALID")
		public Set<Address> otherAddresses1;

		@AssertValid(message = "ASSERT_VALID", requireValidElements = false)
		public Set<Address> otherAddresses2;

		@AssertValid(message = "ASSERT_VALID", requireValidElements = true)
		public Set<Address> otherAddresses3;

	}

	private static class Address
	{
		@NotNull
		public String street;

		@NotNull
		public String city;

		@NotNull
		@Length(max = 6)
		@NotEmpty
		@MatchPattern(pattern = "^[0-9]*$")
		public String zipCode;

		@AssertValid(message = "ASSERT_VALID")
		public Person contact;
	}

	public void testScalarValues()
	{
		final Validator validator = new Validator();

		final Person p = new Person();
		p.firstName = "John";
		p.lastName = "Doe";
		assertTrue(validator.validate(p).size() == 0);

		final Address a = new Address();
		a.street = "The Street";
		a.city = "The City";
		a.zipCode = "12345";
		assertTrue(validator.validate(a).size() == 0);

		// make the address invalid
		a.zipCode = null;
		assertTrue(validator.validate(a).size() == 1);

		// associate the invalid address with the person check the person for validity
		p.homeAddress = a;
		List<ConstraintViolation> violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("ASSERT_VALID"));

		// test circular dependencies
		a.contact = p;
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("ASSERT_VALID"));
	}

	public void testCollectionValues()
	{
		final Validator validator = new Validator();

		final Person p = new Person();
		p.firstName = "John";
		p.lastName = "Doe";
		p.otherAddresses1 = new HashSet<Address>();
		p.otherAddresses2 = new HashSet<Address>();
		p.otherAddresses3 = new HashSet<Address>();

		final Address a = new Address();
		a.street = "The Street";
		a.city = "The City";
		a.zipCode = null;
		assertTrue(validator.validate(a).size() == 1);

		p.otherAddresses1.add(a);
		assertTrue(validator.validate(p).size() == 1);

		p.otherAddresses1.remove(a);
		p.otherAddresses2.add(a);
		assertTrue(validator.validate(p).size() == 0);

		p.otherAddresses3.add(a);
		assertTrue(validator.validate(p).size() == 1);
	}
}
