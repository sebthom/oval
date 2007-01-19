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
import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.guard.Guard.ReportingMode;

/**
 * @author Sebastian Thomschke
 */
public class GuardingWithoutGuardedAnnotationTest extends TestCase
{
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

	public void testConstructorParameterConstraintsInThrowExceptionMode()
	{
		GuardingWithoutGuardedAnnotationAspect.guard.setReportingMode(
				ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION, TestEntity.class);

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
		GuardingWithoutGuardedAnnotationAspect.guard.setReportingMode(
				ReportingMode.NOTIFY_LISTENERS_AND_THROW_EXCEPTION, TestEntity.class);

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
}
