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
import net.sf.oval.annotations.Constrained;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class MethodReturnValueConstraintsValidationTest extends TestCase
{
	@Constrained
	public static class TestEntity
	{

		public String name;

		@NotNull
		@Length(max = 4)
		public String getName()
		{
			return name;
		}
	}

	public void testMethodReturnValueConstraintValidation()
	{
		final TestEntity t = new TestEntity();

		try
		{
			t.getName();
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			assertTrue(e.getConstraintViolations().length == 1);
			assertTrue(e.getConstraintViolations()[0].getCheck() instanceof NotNullCheck);
		}

		t.name = "testtest";

		try
		{
			t.getName();
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			assertTrue(e.getConstraintViolations().length == 1);
			assertTrue(e.getConstraintViolations()[0].getCheck() instanceof LengthCheck);
		}
	}

}
