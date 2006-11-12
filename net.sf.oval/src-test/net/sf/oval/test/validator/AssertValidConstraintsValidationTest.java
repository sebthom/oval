/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
import net.sf.oval.constraints.AssertValid;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;

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
		@RegEx(pattern = "^[0-9]*$")
		public String zipCode;

		@AssertValid(message = "ASSERT_VALID")
		public Person contact;
	}

	public void testFieldValidation()
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
}
