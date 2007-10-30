/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class MethodReturnValueConstraintsValidationTest extends TestCase
{
	@Guarded
	public static class TestEntity
	{
		public String name = "";

		@NotNull(message = "NOT_NULL")
		@Length(max = 4, message = "LENGTH")
		public String getName()
		{
			return name;
		}
	}

	public void testMethodReturnValueConstraintValidation()
	{
		final Guard guard = new Guard();

		TestGuardAspect.aspectOf().setGuard(guard);

		final TestEntity t = new TestEntity();

		try
		{
			t.name = null;
			t.getName();
			fail();
		}
		catch (final ConstraintsViolatedException e)
		{
			assertTrue(e.getConstraintViolations().length == 1);
			assertTrue(e.getConstraintViolations()[0].getMessage().equals("NOT_NULL"));
		}

		t.name = "testtest";

		try
		{
			t.getName();
			fail();
		}
		catch (final ConstraintsViolatedException e)
		{
			assertTrue(e.getConstraintViolations().length == 1);
			assertTrue(e.getConstraintViolations()[0].getMessage().equals("LENGTH"));
		}
	}

}
