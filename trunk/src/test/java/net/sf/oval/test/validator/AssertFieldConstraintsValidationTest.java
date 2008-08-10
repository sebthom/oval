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
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class AssertFieldConstraintsValidationTest extends TestCase
{
	protected static class Person
	{
		@NotNull
		public String firstName;

		@NotNull
		public String lastName;

		@NotNull
		@Length(max = 6, message = "LENGTH")
		@NotEmpty(message = "NOT_EMPTY")
		@MatchPattern(pattern = PATTERN_ZIP_CODE, message = "MATCH_PATTERN")
		public String zipCode;
	}

	private static final String PATTERN_ZIP_CODE = "^[0-9]*$";

	public void testFieldValidation()
	{
		final Validator validator = new Validator();

		// test @NotNull
		final Person p = new Person();
		List<ConstraintViolation> violations = validator.validate(p);
		assertTrue(violations.size() == 3);

		// test @Length(max=)
		p.firstName = "Mike";
		p.lastName = "Mahoney";
		p.zipCode = "1234567";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("LENGTH"));

		// test @NotEmpty
		p.zipCode = "";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("NOT_EMPTY"));

		// test @RegEx
		p.zipCode = "dffd34";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("MATCH_PATTERN"));
	}
}
