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
import net.sf.oval.constraints.Assert;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageJavascriptTest extends TestCase
{
	private static class Person
	{
		@Assert(constraint = "value!=null", message = "CONDITION")
		public String firstName;

		@Assert(constraint = "value!=null")
		public String lastName;

		@Assert(constraint = "value!=null && value.length>0 && value.length<7", message = "CONDITION")
		public String zipCode;
	}

	public void testJavaScriptCondition()
	{
		final Validator validator = new Validator();

		// test not null
		final Person p = new Person();
		List<ConstraintViolation> violations = validator.validate(p);
		assertTrue(violations.size() == 3);
		System.out.println(violations.get(0).getMessage());
		System.out.println(violations.get(1).getMessage());
		System.out.println(violations.get(2).getMessage());
		
		// test max length
		p.firstName = "Mike";
		p.lastName = "Mahoney";
		p.zipCode = "1234567";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("CONDITION"));

		// test not empty
		p.zipCode = "";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("CONDITION"));
		
		// test ok
		p.zipCode = "wqeew";
		violations = validator.validate(p);
		assertTrue(violations.size() == 0);
	}
}
