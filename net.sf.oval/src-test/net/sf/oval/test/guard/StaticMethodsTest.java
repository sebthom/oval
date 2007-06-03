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
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class StaticMethodsTest extends TestCase
{
	@Guarded
	private static class TestEntity
	{
		@NotNull(message = "NULL")
		public static String value;

		public static void setValue(@AssertFieldConstraints
		String value)
		{
			TestEntity.value = value;
		}

		public static void doSomethingPre()
		{
		//
		}

		public static void doSomethingPost()
		{
		//
		}
	}

	public void testPreValidateThis() throws Exception
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);
		
		TestEntity.value = null;

		try
		{
			TestEntity.doSomethingPre();
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertTrue(ex.getConstraintViolations().length == 1);
			assertTrue(ex.getConstraintViolations()[0].getMessage().equals("NULL"));
		}

		TestEntity.value = "";
		TestEntity.doSomethingPre();
	}

	public void testPostValidateThis() throws Exception
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);
		
		TestEntity.value = null;

		try
		{
			TestEntity.doSomethingPost();
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertTrue(ex.getConstraintViolations().length == 1);
			assertTrue(ex.getConstraintViolations()[0].getMessage().equals("NULL"));
		}

		TestEntity.value = "";
		TestEntity.doSomethingPost();
	}

	public void testSetterValidation() throws Exception
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);
		
		try
		{
			TestEntity.setValue(null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			assertTrue(ex.getConstraintViolations().length == 1);
			assertTrue(ex.getConstraintViolations()[0].getMessage().equals("NULL"));
		}
	}
}