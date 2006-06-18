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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.exceptions.ConstraintAnnotationNotPresentException;
import net.sf.oval.exceptions.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class AddingConstraintsTest extends TestCase
{
	@Constrained()
	protected static class TestEntity1
	{
		protected String name;

		private TestEntity1(String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}

	@Constrained()
	protected static class TestEntity2
	{
		protected String name;

		private TestEntity2(String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}

	@Constrained()
	protected static class TestEntity3
	{
		protected String name;

		private TestEntity3(String name)
		{
			this.name = name;
		}

		/**
		 * @param name the name to set
		 */
		public void setName(String name)
		{
			this.name = name;
		}
	}

	/**
	 * try to programmatically add a NotNull constraint to the setter parameter
	 */
	public void addConstraintToMethodParameter()
	{
		try
		{
			Method setter = TestEntity1.class.getDeclaredMethod("setName",
					new Class[]{String.class});
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");

			// testing without constraint
			try
			{
				TestEntity1 entity = new TestEntity1("blabla");
				entity.setName(null);
			}
			catch (ConstraintsViolatedException e)
			{
				fail();
			}

			// adding a constraint
			Validator.addCheck(setter, 0, notNullCheck);
			try
			{
				TestEntity1 entity = new TestEntity1("blabla");
				entity.setName(null);
				fail();
			}
			catch (ConstraintsViolatedException e)
			{
				ConstraintViolation[] violations = e.getConstraintViolations();
				assertTrue(violations.length == 1);
				assertTrue(violations[0].getContext() instanceof MethodParameterContext);
				assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			}

			// removing the constraint
			Validator.removeCheck(setter, 0, notNullCheck);
			try
			{
				TestEntity1 entity = new TestEntity1("blabla");
				entity.setName(null);
			}
			catch (ConstraintsViolatedException e)
			{
				fail();
			}
		}
		catch (ConstraintAnnotationNotPresentException e)
		{
			fail();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * try to programmatically add a NotNull constraint to the constructor parameter
	 */
	public void testAddConstraintToConstructorParameter()
	{
		try
		{
			Constructor constructor = TestEntity2.class
					.getDeclaredConstructor(new Class[]{String.class});
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");

			// testing without constraint
			try
			{
				new TestEntity2(null);
			}
			catch (ConstraintsViolatedException e)
			{
				fail();
			}

			// adding a constraint
			Validator.addCheck(constructor, 0, notNullCheck);
			try
			{
				new TestEntity2(null);
				fail();
			}
			catch (ConstraintsViolatedException e)
			{
				ConstraintViolation[] violations = e.getConstraintViolations();
				assertTrue(violations.length == 1);
				assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
				assertTrue(violations[0].getCheck() instanceof NotNullCheck);
			}

			// removing the constraint
			Validator.removeCheck(constructor, 0, notNullCheck);
			try
			{
				new TestEntity2(null);
			}
			catch (ConstraintsViolatedException e)
			{
				fail();
			}
		}
		catch (ConstraintAnnotationNotPresentException e)
		{
			fail();
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (NoSuchMethodException e)
		{
			e.printStackTrace();
			fail();
		}
	}

	/**
	 * programmatically add a NotNull constraint to the name field
	 */
	public void testAddConstraintToField()
	{
		TestEntity3 entity = new TestEntity3(null);
		assertTrue(Validator.validate(entity).size() == 0);

		try
		{
			Field field = TestEntity3.class.getDeclaredField("name");
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");

			// testing without constraint
			{
				List<ConstraintViolation> violations = Validator.validate(entity);
				assertTrue(violations.size() == 0);
			}

			// adding a constraint
			{
				Validator.addCheck(field, notNullCheck);

				List<ConstraintViolation> violations = Validator.validate(entity);
				assertTrue(violations.size() == 1);
				assertTrue(violations.get(0).getCheck() instanceof NotNullCheck);
			}

			// removing the constraint
			{
				Validator.removeCheck(field, notNullCheck);

				List<ConstraintViolation> violations = Validator.validate(entity);
				assertTrue(violations.size() == 0);
			}
		}
		catch (SecurityException e)
		{
			e.printStackTrace();
			fail();
		}
		catch (NoSuchFieldException e)
		{
			e.printStackTrace();
			fail();
		}
	}
}
