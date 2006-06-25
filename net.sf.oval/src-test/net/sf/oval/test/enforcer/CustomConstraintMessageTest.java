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
import net.sf.oval.ConstraintViolation;
import net.sf.oval.ConstraintsEnforcer;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.Range;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class CustomConstraintMessageTest extends TestCase
{
	@Constrained(applyFieldConstraintsToSetter = true)
	private class TestEntity
	{
		@Range(min = 2, max = 4, message = "An amount of {1} in not in the allowed range ({2}-{3})")
		private int amount;

		@NotNull(message = CUSTOM_ERROR_MESSAGE)
		private String name;

		/**
		 * @return the amount
		 */
		public int getAmount()
		{
			return amount;
		}

		/**
		 * @return the name
		 */
		public String getName()
		{
			return name;
		}

		/**
		 * @param amount the amount to set
		 */
		public void setAmount(int amount)
		{
			this.amount = amount;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}

	}

	private final static String CUSTOM_ERROR_MESSAGE = "The property [name] cannot be null!";
	private final static String EXPECTED_RANGE_MESSAGE = "An amount of 5 in not in the allowed range (2-4)";

	/**
	 * check that custom messages are used correctly
	 */
	public void testCustomConstraintMessage()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(ConstraintsEnforcer.Mode.THROW_EXCEPTION);

		final TestEntity e = new TestEntity();

		try
		{
			e.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);

			if (!CUSTOM_ERROR_MESSAGE.equals(violations[0].getMessage()))
				fail("The returned error message <" + violations[0].getMessage()
						+ "> does not equal the specified custom error message <"
						+ CUSTOM_ERROR_MESSAGE + ">");
		}

		try
		{
			e.setAmount(5);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);

			if (!EXPECTED_RANGE_MESSAGE.equals(violations[0].getMessage()))
				fail("The returned error message <" + violations[0].getMessage()
						+ "> does not equal the specified custom error message <"
						+ EXPECTED_RANGE_MESSAGE + ">");
		}
	}
}
