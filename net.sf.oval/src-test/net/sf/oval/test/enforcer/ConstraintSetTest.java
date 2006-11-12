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
import net.sf.oval.annotations.DefineConstraintSet;
import net.sf.oval.constraints.AssertConstraintSet;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetTest extends TestCase
{
	@Constrained
	private class Person
	{
		@DefineConstraintSet("zipCode")
		@NotNull(message = "NOT_NULL")
		@Length(max = 6, message = "LENGTH")
		@NotEmpty(message = "NOT_EMPTY")
		@RegEx(pattern = "^[0-9]*$", message = "REG_EX")
		private String zipCode;

		public String getZipCode()
		{
			return zipCode;
		}

		public void setZipCode(@AssertConstraintSet(id = "zipCode")
		String zipCode)
		{
			this.zipCode = zipCode;
		}
	}

	@Constrained
	private class Person2
	{
		private String zipCode;

		public String getZipCode()
		{
			return zipCode;
		}

		public void setZipCode(@AssertConstraintSet(source = Person.class, id = "zipCode")
		String zipCode)
		{
			this.zipCode = zipCode;
		}
	}

	public void testConstraintSetValidation()
	{
		{
			final Person p = new Person();

			TestEnforcerAspect.constraintsEnforcer.setReportingMode(
					ConstraintsEnforcer.ReportingMode.NOTIFY_LISTENERS, p);
			final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
			TestEnforcerAspect.constraintsEnforcer.addListener(va, p);

			// test @Length(max=)
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
		}

		{
			final Person2 p = new Person2();

			TestEnforcerAspect.constraintsEnforcer.setReportingMode(
					ConstraintsEnforcer.ReportingMode.NOTIFY_LISTENERS, p);
			final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
			TestEnforcerAspect.constraintsEnforcer.addListener(va, p);

			// test @Length(max=)
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
		}
	}
}
