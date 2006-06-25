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

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.ConstraintsViolatedAdapter;
import net.sf.oval.ConstraintsEnforcer.Mode;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.constraints.Length;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotNull;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class ParameterConstraintsTest extends TestCase
{
	@Constrained
	public static class TestEntity
	{
		@SuppressWarnings("unused")
		@NotNull
		private String name;

		/**
		 * Constructor 1
		 * 
		 * @param name
		 */
		public TestEntity(@NotNull
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

		public void setName(@NotNull
		@Length(max = 4)
		String name)
		{
			this.name = name;
		}
	}

	public void testConstructorParameterConstraintsInNotifyListenersMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.NOTIFY_LISTENERS, TestEntity.class);

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
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
		}

		try
		{
			new TestEntity("test");
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}

		/*
		 * Testing Constructor 2
		 */
		try
		{
			// the constructor should not result in an any auto validation,
			// therefore the construction with a null value for the name parameter should succeed
			new TestEntity(null, 100);
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}
	}

	public void testConstructorParameterConstraintsInThrowExceptionMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.THROW_EXCEPTION, TestEntity.class);

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
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
		}

		try
		{
			new TestEntity("test");
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}

		/*
		 * Testing Constructor 2
		 */
		try
		{
			// the constructor should not result in an any auto validation,
			// therefore the construction with a null value for the name parameter should succeed
			new TestEntity(null, 100);
		}
		catch (ConstraintsViolatedException e)
		{
			fail();
		}
	}

	public void testMethodParametersInThrowExceptionMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.THROW_EXCEPTION, TestEntity.class);

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
			assertTrue(violations[0].getCheck() instanceof NotNullCheck);
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
			assertTrue(violations[0].getCheck() instanceof LengthCheck);
		}
	}

	public void testMethodParametersInNotifyListenersMode()
	{
		TestEnforcerAspect.constraintsEnforcer.setMode(Mode.NOTIFY_LISTENERS, TestEntity.class);

		TestEntity entity = new TestEntity("");

		ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
		TestEnforcerAspect.constraintsEnforcer.addListener(va, entity);

		entity.setName(null);
		entity.setName("12345678");
		List<ConstraintViolation> violations = va.getConstraintViolations();
		assertTrue(violations.size() == 2);
		assertTrue(violations.get(0).getCheck() instanceof NotNullCheck);
		assertTrue(violations.get(1).getCheck() instanceof LengthCheck);

		TestEnforcerAspect.constraintsEnforcer.removeListener(va, entity);
	}
}
