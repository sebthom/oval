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
package net.sf.oval.test.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.exceptions.ConstrainedAnnotationNotPresentException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class AddingConstraintsTest extends TestCase
{
	protected static class TestEntity
	{
		protected String name;

		private TestEntity(String name)
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
	 * try to programmatically add a NotNull constraint to the constructor parameter
	 * this should fail since the class is not annotated with @Constrain and constructor parameter constraints are not enforced via the ValidationAspect
	 */
	public void testAddConstraintToConstructorParameter()
	{
		final Validator validator = new Validator();

		try
		{
			Constructor constructor = TestEntity.class
					.getDeclaredConstructor(new Class<?>[]{String.class});
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");
			validator.addCheck(constructor, 0, notNullCheck);
			fail();
		}
		catch (ConstrainedAnnotationNotPresentException e)
		{
			//expected
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
		final Validator validator = new Validator();

		TestEntity entity = new TestEntity(null);
		assertTrue(validator.validate(entity).size() == 0);

		try
		{
			Field field = TestEntity.class.getDeclaredField("name");
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");
			validator.addCheck(field, notNullCheck);

			List<ConstraintViolation> violations = validator.validate(entity);
			assertTrue(violations.size() == 1);
			assertTrue(violations.get(0).getCheck() instanceof NotNullCheck);
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

	/**
	 * try to programmatically add a NotNull constraint to the setter parameter
	 * this should fail since the class is not annotated with @Constrain and constructor parameter constraints are not enforced via the ValidationAspect
	 */
	public void testAddConstraintToMethodParameter()
	{
		final Validator validator = new Validator();

		try
		{
			Method setter = TestEntity.class
					.getDeclaredMethod("setName", new Class<?>[]{String.class});
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");
			validator.addCheck(setter, 0, notNullCheck);
			fail();
		}
		catch (ConstrainedAnnotationNotPresentException e)
		{
			//expected
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
}
