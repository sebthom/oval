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
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraints.AssertConstraintSet;
import net.sf.oval.constraints.ConstraintSet;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotEmpty;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.RegEx;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetTest extends TestCase
{
	@Guarded
	private class Person
	{
		@ConstraintSet("zipCode")
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

	@Guarded
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

			TestGuardAspect.guard.setReportingMode(
					Guard.ReportingMode.NOTIFY_LISTENERS, p);
			final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
			TestGuardAspect.guard.addListener(va, p);

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

			TestGuardAspect.guard.setReportingMode(
					Guard.ReportingMode.NOTIFY_LISTENERS, p);
			final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
			TestGuardAspect.guard.addListener(va, p);

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
