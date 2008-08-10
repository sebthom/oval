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
package net.sf.oval.test.guard;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.AssertFieldConstraintsCheck;
import net.sf.oval.constraint.AssertTrue;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ApplyFieldConstraintsToParametersTest extends TestCase
{
	@Guarded
	protected static class Person
	{
		@NotNull(message = "NOT_NULL")
		private String firstName = "";

		@AssertTrue(message = "ASSERT_TRUE")
		private boolean isValid = true;

		@NotNull(message = "NOT_NULL")
		private String lastName = "";

		@NotNull(message = "NOT_NULL")
		@Length(max = 6, message = "LENGTH")
		@NotEmpty(message = "NOT_EMPTY")
		@MatchPattern(pattern = "^[0-9]*$", message = "REG_EX")
		private String zipCode = "1";

		public String getFirstName()
		{
			return firstName;
		}

		public String getLastName()
		{
			return lastName;
		}

		public String getZipCode()
		{
			return zipCode;
		}

		public boolean isValid()
		{
			return isValid;
		}

		public void setDummyFirstName(@AssertFieldConstraints(value = "firstName") final String dummyFirstName)
		{
		// doing interesting stuff here
		}

		public void setFirstName(@AssertFieldConstraints final String firstName)
		{
			this.firstName = firstName;
		}

		public void setLastName(@AssertFieldConstraints final String lastName)
		{
			this.lastName = lastName;
		}

		public void setValid(@AssertFieldConstraints final boolean isValid)
		{
			this.isValid = isValid;
		}

		public void setZipCode(@AssertFieldConstraints final String zipCode)
		{
			this.zipCode = zipCode;
		}

		public void setZipCode2(final String zipCode)
		{
			this.zipCode = zipCode;
		}
	}

	/**
	 * by default constraints specified for a field are also used for validating
	 * method parameters of the corresponding setter methods 
	 */
	public void testSetterValidation() throws Exception
	{
		final Person p = new Person();

		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		guard.enableProbeMode(p);

		final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		guard.addListener(va, p);

		// test @Length(max=)
		p.setFirstName("Mike");
		p.setLastName("Mahoney");
		p.setZipCode("1234567");
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("LENGTH"));
		va.clear();

		// test @NotEmpty
		p.setZipCode("");
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_EMPTY"));
		va.clear();

		// test @RegEx
		p.setZipCode("dffd34");
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("REG_EX"));
		va.clear();

		// test @AssertTrue
		p.setValid(false);
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("ASSERT_TRUE"));
		va.clear();

		// test @FieldConstraint("fieldname")
		p.setDummyFirstName(null);
		assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
		assertTrue(va.getConstraintViolations().size() == 1);
		assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
		va.clear();

		// test dynamic introduction of FieldConstraintsCheck
		{
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		}
		{
			final Method setter = p.getClass().getMethod("setZipCode2", new Class< ? >[]{String.class});
			final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
			guard.addChecks(setter, 0, check);
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getMessage().equals("REG_EX"));
			va.clear();
			guard.removeChecks(setter, 0, check);
		}
		{
			final Method setter = p.getClass().getMethod("setZipCode2", new Class< ? >[]{String.class});
			final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
			check.setFieldName("firstName");
			guard.addChecks(setter, 0, check);
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
			p.setZipCode2(null);
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
			va.clear();
			guard.removeChecks(setter, 0, check);
		}
		{
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		}
	}
}
