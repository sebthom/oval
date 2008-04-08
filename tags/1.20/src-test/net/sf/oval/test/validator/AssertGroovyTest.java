/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

/**
 * @author Sebastian Thomschke
 */
public class AssertGroovyTest extends TestCase
{
	@net.sf.oval.constraint.Assert(expr = "_this.firstName!=null && _this.lastName!=null && (_this.firstName.length() + _this.lastName.length() > 9)", lang = "groovy", errorCode = "C0")
	private static class Person
	{
		@net.sf.oval.constraint.Assert(expr = "_value!=null", lang = "groovy", errorCode = "C1")
		public String firstName;

		@net.sf.oval.constraint.Assert(expr = "_value!=null", lang = "groovy", errorCode = "C2")
		public String lastName;

		@net.sf.oval.constraint.Assert(expr = "_value!=null && _value.length()>0 && _value.length()<7", lang = "groovy", errorCode = "C3")
		public String zipCode;
	}

	public void testGroovyExpression()
	{
		final Validator validator = new Validator();

		// test not null
		final Person p = new Person();
		List<ConstraintViolation> violations = validator.validate(p);
		assertTrue(violations.size() == 4);

		// test max length
		p.firstName = "Mike";
		p.lastName = "Mahoney";
		p.zipCode = "1234567";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getErrorCode().equals("C3"));

		// test not empty
		p.zipCode = "";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getErrorCode().equals("C3"));

		// test ok
		p.zipCode = "wqeew";
		violations = validator.validate(p);
		assertTrue(violations.size() == 0);

		// test object-level constraint
		p.firstName = "12345";
		p.lastName = "1234";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getErrorCode().equals("C0"));
	}
}
