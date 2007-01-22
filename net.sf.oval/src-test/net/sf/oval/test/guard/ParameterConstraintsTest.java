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

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ParameterConstraintsTest extends TestCase
{
	@Guarded
	public static class TestEntity
	{
		@SuppressWarnings("unused")
		@NotNull(message = "NOT_NULL")
		private String name;

		/**
		 * Constructor 1
		 * 
		 * @param name
		 */
		public TestEntity(@NotNull(message = "NOT_NULL")
		String name)
		{
			this.name = name;
		}

		/**
		 * Constructor 2
		 * 
		 * @param name
		 * @param bla
		 */
		public TestEntity(String name, int bla)
		{
			this.name = name;
		}

		public void setName(@NotNull(message = "NOT_NULL")
		@Length(max = 4, message = "LENGTH")
		String name)
		{
			this.name = name;
		}
	}

	public void testConstructorParameterConstraintsInSwallowExceptionMode()
	{
		TestGuardAspect.guard.setSwallowPreConditionExceptions(TestEntity.class, true);

		/*
		 * Testing Constructor 1
		 */
		try
		{
			new TestEntity(null);

			// even in silent mode an exception should be thrown to prevent the returning of a reference to that new object instance
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
			assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
		}

		new TestEntity("test");

		/*
		 * Testing Constructor 2
		 */
		// the constructor should not result in an any auto validation,
		// therefore the construction with a null value for the name parameter should succeed
		new TestEntity(null, 100);
	}

	public void testConstructorParameterConstraintsInThrowExceptionMode()
	{
		TestGuardAspect.guard.setSwallowPreConditionExceptions(TestEntity.class, false);

		/*
		 * Testing Constructor 1
		 */
		try
		{
			new TestEntity(null);
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length == 1);
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
			assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
		}

		new TestEntity("test");

		/*
		 * Testing Constructor 2
		 */
		// the constructor should not result in an any auto validation,
		// therefore the construction with a null value for the name parameter should succeed
		new TestEntity(null, 100);
	}

	public void testMethodParametersInThrowExceptionMode()
	{
		TestGuardAspect.guard.setSwallowPreConditionExceptions(TestEntity.class, false);

		try
		{
			TestEntity t1 = new TestEntity("");
			t1.setName(null);
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length > 0);
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
		}

		try
		{
			TestEntity t1 = new TestEntity("");
			t1.setName("12345678");
			fail();
		}
		catch (ConstraintsViolatedException e)
		{
			ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations != null && violations.length > 0);
			assertTrue(violations[0].getMessage().equals("LENGTH"));
		}
	}

	public void testMethodParametersInSwallowExceptionMode()
	{
		TestGuardAspect.guard.setSwallowPreConditionExceptions(TestEntity.class, true);

		TestEntity entity = new TestEntity("");

		ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		TestGuardAspect.guard.addListener(va, entity);

		entity.setName(null);
		entity.setName("12345678");
		List<ConstraintViolation> violations = va.getConstraintViolations();
		assertTrue(violations.size() == 2);
		assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));
		assertTrue(violations.get(1).getMessage().equals("LENGTH"));

		TestGuardAspect.guard.removeListener(va, entity);
	}
}
