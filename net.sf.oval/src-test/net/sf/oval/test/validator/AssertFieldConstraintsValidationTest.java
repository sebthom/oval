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
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotEmptyCheck;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;
import net.sf.oval.constraints.RegExCheck;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class AssertFieldConstraintsValidationTest extends TestCase
{
	private final static String REGEX_ZIP_CODE = "^[0-9]*$";

	private static class Person
	{
		@NotNull
		public String firstName;

		@NotNull
		public String lastName;

		@NotNull
		@Length(max = 6)
		@NotEmpty
		@RegEx(pattern = REGEX_ZIP_CODE)
		public String zipCode;
	}

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
		assertTrue(violations.get(0).getCheck() instanceof LengthCheck);

		// test @NotEmpty
		p.zipCode = "";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getCheck() instanceof NotEmptyCheck);

		// test @RegEx
		p.zipCode = "dffd34";
		violations = validator.validate(p);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getCheck() instanceof RegExCheck);
	}
}
