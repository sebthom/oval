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
package net.sf.oval.test.validator;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ClassChecks;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.checks.NotNullCheck;
import net.sf.oval.exceptions.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
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
	 * this should fail since the class is not annotated with @Guarded and constructor parameter constraints are not enforced via the GuardAspect
	 */
	public void testAddConstraintToConstructorParameter() throws Exception
	{
		final Validator validator = new Validator();

		try
		{
			Constructor constructor = TestEntity.class
					.getDeclaredConstructor(new Class<?>[]{String.class});

			final ClassChecks cc = validator.getClassChecks(constructor.getDeclaringClass());
			cc.addChecks(constructor, 0, new NotNullCheck());
			fail();
		}
		catch (InvalidConfigurationException e)
		{
			//expected
		}
	}

	/**
	 * programmatically add a NotNull constraint to the name field
	 */
	public void testAddConstraintToField() throws Exception
	{
		final Validator validator = new Validator();

		TestEntity entity = new TestEntity(null);
		assertTrue(validator.validate(entity).size() == 0);

		Field field = TestEntity.class.getDeclaredField("name");
		NotNullCheck notNullCheck = new NotNullCheck();
		notNullCheck.setMessage("NOT_NULL");
		
		final ClassChecks cc = validator.getClassChecks(field.getDeclaringClass());
		cc.addChecks(field, notNullCheck);

		List<ConstraintViolation> violations = validator.validate(entity);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));
	}

	/**
	 * try to programmatically add a NotNull constraint to the setter parameter
	 * this should fail since the class is not annotated with @Guarded and constructor parameter constraints are not enforced via the GuardAspect
	 */
	public void testAddConstraintToMethodParameter() throws Exception
	{
		final Validator validator = new Validator();

		try
		{
			Method setter = TestEntity.class
					.getDeclaredMethod("setName", new Class<?>[]{String.class});
			NotNullCheck notNullCheck = new NotNullCheck();
			notNullCheck.setMessage("name must not be null");
			
			final ClassChecks cc = validator.getClassChecks(setter.getDeclaringClass());
			cc.addChecks(setter, 0, notNullCheck);
			fail();
		}
		catch (InvalidConfigurationException e)
		{
			//expected
		}
	}
}
