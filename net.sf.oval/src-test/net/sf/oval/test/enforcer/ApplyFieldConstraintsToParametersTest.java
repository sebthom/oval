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
package net.sf.oval.test.enforcer;

import java.lang.reflect.Method;

import junit.framework.TestCase;
import net.sf.oval.ConstraintsEnforcer;
import net.sf.oval.ConstraintsViolatedAdapter;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.constraints.AssertFieldConstraints;
import net.sf.oval.constraints.AssertFieldConstraintsCheck;
import net.sf.oval.constraints.AssertTrue;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class ApplyFieldConstraintsToParametersTest extends TestCase
{
	@Constrained
	private class Person
	{
		@NotNull(message="NOT_NULL")
		private String firstName;

		@AssertTrue(message="ASSERT_TRUE")
		private boolean isValid = true;

		@NotNull(message="NOT_NULL")
		private String lastName;

		@NotNull(message="NOT_NULL")
		@Length(max = 6,message="LENGTH")
		@NotEmpty(message="NOT_EMPTY")
		@RegEx(pattern = "^[0-9]*$",message="REG_EX")
		private String zipCode;

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

		public void setDummyFirstName(@AssertFieldConstraints("firstName")
		String dummyFirstName)
		{
		// doing interesting stuff here
		}

		public void setFirstName(@AssertFieldConstraints
		String firstName)
		{
			this.firstName = firstName;
		}

		public void setLastName(@AssertFieldConstraints
		String lastName)
		{
			this.lastName = lastName;
		}

		public void setValid(@AssertFieldConstraints
		boolean isValid)
		{
			this.isValid = isValid;
		}

		public void setZipCode(@AssertFieldConstraints
		String zipCode)
		{
			this.zipCode = zipCode;
		}

		public void setZipCode2(String zipCode)
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

		TestEnforcerAspect.constraintsEnforcer
				.setReportingMode(ConstraintsEnforcer.ReportingMode.NOTIFY_LISTENERS, p);
		final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		TestEnforcerAspect.constraintsEnforcer.addListener(va, p);

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
			final Method setter = p.getClass().getMethod("setZipCode2",
					new Class<?>[]{String.class});
			final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
			TestEnforcerAspect.validator.addChecks(setter, 0, check);
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getMessage().equals("REG_EX"));
			va.clear();
			TestEnforcerAspect.validator.removeCheck(setter, 0, check);
		}
		{
			final Method setter = p.getClass().getMethod("setZipCode2",
					new Class<?>[]{String.class});
			final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
			check.setFieldName("firstName");
			TestEnforcerAspect.validator.addChecks(setter, 0, check);
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
			p.setZipCode2(null);
			assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
			assertTrue(va.getConstraintViolations().size() == 1);
			assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
			va.clear();
			TestEnforcerAspect.validator.removeCheck(setter, 0, check);
		}
		{
			p.setZipCode2("dffd34");
			assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
		}
	}
}
