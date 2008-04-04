/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class AddingChecksTest extends TestCase
{
	@Guarded
	protected static class TestEntity1
	{
		protected String name;

		protected TestEntity1(final String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}
	}

	@Guarded
	protected static class TestEntity2
	{
		protected String name;

		protected TestEntity2(final String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}
	}

	@Guarded()
	protected static class TestEntity3
	{
		protected String name;

		protected TestEntity3(final String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(final String name)
		{
			this.name = name;
		}
	}

	/**
	 * try to programmatically add a NotNull constraint to the setter parameter
	 */
	public void addConstraintToMethodParameter()
	{
		final Guard guard = TestGuardAspect.aspectOf().getGuard();

		try
		{
			final Method setter = TestEntity1.class.getDeclaredMethod("setName",
					new Class[]{String.class});
			final NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("NOT_NULL");

			// testing without constraint
			try
			{
				final TestEntity1 entity = new TestEntity1("blabla");
				entity.setName(null);
			}
			catch (final ConstraintsViolatedException e)
			{
				fail();
			}

			// adding a constraint
			guard.addChecks(setter, 0, notNullCheck);
			try
			{
				final TestEntity1 entity = new TestEntity1("blabla");
				entity.setName(null);
				fail();
			}
			catch (final ConstraintsViolatedException e)
			{
				final ConstraintViolation[] violations = e.getConstraintViolations();
				assertTrue(violations.length == 1);
				assertTrue(violations[0].getContext() instanceof MethodParameterContext);
				assertTrue(violations[0].getMessage().equals("NOT_NULL"));
			}

			// removing the constraint
			guard.removeChecks(setter, 0, notNullCheck);
			try
			{
				final TestEntity1 entity = new TestEntity1("blabla");
				entity.setName(null);
			}
			catch (final ConstraintsViolatedException e)
			{
				fail();
			}
		}
		catch (final InvalidConfigurationException e)
		{
			fail();
		}
		catch (final SecurityException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (final NoSuchMethodException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * try to programmatically add a NotNull constraint to the constructor parameter
	 */
	public void testAddConstraintToConstructorParameter() throws Exception
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		final Constructor constructor = TestEntity2.class
				.getDeclaredConstructor(new Class[]{String.class});
		final NotNullCheck notNullCheck = new NotNullCheck();
		notNullCheck.setMessage("NOT_NULL");

		// testing without constraint
		new TestEntity2(null);

		// adding a constraint
		guard.addChecks(constructor, 0, notNullCheck);
		try
		{
			new TestEntity2(null);
			fail();
		}
		catch (final ConstraintsViolatedException e)
		{
			final ConstraintViolation[] violations = e.getConstraintViolations();
			assertTrue(violations.length == 1);
			assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
			assertTrue(violations[0].getMessage().equals("NOT_NULL"));
		}

		// removing the constraint
		guard.removeChecks(constructor, 0, notNullCheck);

		new TestEntity2(null);
	}

	/**
	 * programmatically add a NotNull constraint to the name field
	 */
	public void testAddConstraintToField() throws Exception
	{
		final Guard guard = new Guard();
		TestGuardAspect.aspectOf().setGuard(guard);

		final TestEntity3 entity = new TestEntity3(null);
		assertEquals(0, guard.validate(entity).size());

		final Field field = TestEntity3.class.getDeclaredField("name");
		final NotNullCheck notNullCheck = new NotNullCheck();
		notNullCheck.setMessage("NOT_NULL");

		// testing without constraint
		{
			final List<ConstraintViolation> violations = guard.validate(entity);
			assertTrue(violations.size() == 0);
		}

		// adding a constraint
		{
			guard.addChecks(field, notNullCheck);

			final List<ConstraintViolation> violations = TestGuardAspect.aspectOf().getGuard()
					.validate(entity);
			assertTrue(violations.size() == 1);
			assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));
		}

		// removing the constraint
		{
			guard.removeChecks(field, notNullCheck);

			final List<ConstraintViolation> violations = TestGuardAspect.aspectOf().getGuard()
					.validate(entity);
			assertTrue(violations.size() == 0);
		}
	}
}
