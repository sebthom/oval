/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2013 Sebastian
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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.AssertTrue;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ApplyFieldConstraintsToConstructorsTest extends TestCase
{
	@Guarded(applyFieldConstraintsToConstructors = true, checkInvariants = false)
	private static class Person
	{
		@SuppressWarnings("unused")
		@AssertTrue(message = "ASSERT_TRUE")
		private boolean isValid = true;

		@SuppressWarnings("unused")
		@NotNull(message = "NOT_NULL")
		private String firstName = "";

		@SuppressWarnings("unused")
		@NotNull(message = "NOT_NULL")
		private String lastName = "";

		@SuppressWarnings("unused")
		@NotNull(message = "NOT_NULL")
		@Length(max = 6, message = "LENGTH")
		@NotEmpty(message = "NOT_EMPTY")
		@MatchPattern(pattern = "^[0-9]*$", message = "REG_EX")
		private String zipCode = "1";

		protected Person(final boolean isValid, final String firstName, final String lastName, final String zipCode)
		{
			super();
			this.isValid = isValid;
			this.firstName = firstName;
			this.lastName = lastName;
			this.zipCode = zipCode;
		}

		protected Person(final String theFirstName, final String theLastName, final String theZipCode)
		{
			super();
			firstName = theFirstName;
			lastName = theLastName;
			zipCode = theZipCode;
		}
	}

	/**
	 * by default constraints specified for a field are also used for validating
	 * method parameters of the corresponding setter methods
	 */
	@SuppressWarnings("unused")
	public void testConstrucorParameterValidation()
	{
		try
		{
			new Person(false, null, null, null);
		}
		catch (final ConstraintsViolatedException ex)
		{
			assertEquals(ex.getConstraintViolations().length, 4);
		}

		try
		{
			new Person(true, "", "", "12345");
		}
		catch (final ConstraintsViolatedException ex)
		{
			fail();
		}

		try
		{
			new Person(null, null, null);
		}
		catch (final ConstraintsViolatedException ex)
		{
			fail();
		}

	}
}
