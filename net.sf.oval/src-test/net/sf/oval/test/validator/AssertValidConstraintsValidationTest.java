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
import net.sf.oval.constraints.AssertValidCheck;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class AssertValidConstraintsValidationTest extends TestCase
{
	private static class Person
	{
		@NotNull
		public String firstName;

		@NotNull
		public String lastName;

		@AssertValid
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

		@AssertValid
		public Person contact;
	}

	public void testFieldValidation()
	{
		final Person p = new Person();
		p.firstName = "John";
		p.lastName = "Doe";
		assertTrue(Validator.validate(p).size() == 0);

		final Address a = new Address();
		a.street = "The Street";
		a.city = "The City";
		a.zipCode = "12345";
		assertTrue(Validator.validate(a).size() == 0);

		// make the address invalid
		a.zipCode = null;
		assertTrue(Validator.validate(a).size() == 1);

		// associate the invalid address with the person check the person for validity
		p.homeAddress = a;
		List<ConstraintViolation> violations = Validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getCheck() instanceof AssertValidCheck);

		// test circular dependencies
		a.contact = p;
		violations = Validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getCheck() instanceof AssertValidCheck);
	}
}
