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

import junit.framework.TestCase;
import net.sf.oval.ConstraintsEnforcer;
import net.sf.oval.ConstraintsViolatedAdapter;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.constraints.AssertTrue;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class ApplyFieldConstraintsToSetterTest extends TestCase
{
	@Constrained(applyFieldConstraintsToSetter = true)
	private class Person
	{
		@AssertTrue(message = "ASSERT_TRUE")
		private boolean isValid = true;

		@NotNull(message = "NOT_NULL")
		private String firstName;

		@NotNull(message = "NOT_NULL")
		private String lastName;

		@NotNull(message = "NOT_NULL")
		@Length(max = 6, message = "LENGTH")
		@NotEmpty(message = "NOT_EMPTY")
		@RegEx(pattern = "^[0-9]*$", message = "REG_EX")
		private String zipCode;

		public String getFirstName()
		{
			return firstName;
		}

		public void setFirstName(String firstName)
		{
			this.firstName = firstName;
		}

		public String getLastName()
		{
			return lastName;
		}

		public void setLastName(String lastName)
		{
			this.lastName = lastName;
		}

		public String getZipCode()
		{
			return zipCode;
		}

		public void setZipCode(String zipCode)
		{
			this.zipCode = zipCode;
		}

		public boolean isValid()
		{
			return isValid;
		}

		public void setValid(boolean isValid)
		{
			this.isValid = isValid;
		}
	}

	/**
	 * by default constraints specified for a field are also used for validating
	 * method parameters of the corresponding setter methods 
	 */
	public void testSetterValidation()
	{
		final Person p = new Person();

		TestEnforcerAspect.constraintsEnforcer.setReportingMode(
				ConstraintsEnforcer.ReportingMode.NOTIFY_LISTENERS, p);
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
	}
}
