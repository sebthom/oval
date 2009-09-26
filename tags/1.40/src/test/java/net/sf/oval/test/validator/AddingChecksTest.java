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
package net.sf.oval.test.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
 */
public class AddingChecksTest extends TestCase
{
	protected static class TestEntity
	{
		protected String name;

		protected TestEntity(final String name)
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
	 * programmatically add a NotNull constraint to the name field
	 */
	public void testAddConstraintToField() throws Exception
	{
		final Validator validator = new Validator();

		final TestEntity entity = new TestEntity(null);
		assertTrue(validator.validate(entity).size() == 0);

		final Field field = TestEntity.class.getDeclaredField("name");
		final NotNullCheck notNullCheck = new NotNullCheck();
		notNullCheck.setMessage("NOT_NULL");

		validator.addChecks(field, notNullCheck);

		final List<ConstraintViolation> violations = validator.validate(entity);
		assertTrue(violations.size() == 1);
		assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));
	}

	/**
	 * try to programmatically add a NotNull constraint to the void setter
	 * this should fail since the method is not a getter
	 */
	public void testAddConstraintToGetter() throws Exception
	{
		final Validator validator = new Validator();

		try
		{
			final Method setter = TestEntity.class.getDeclaredMethod("setName",
					new Class< ? >[]{String.class});

			validator.addChecks(setter, new NotNullCheck());
			fail();
		}
		catch (final InvalidConfigurationException e)
		{
			// expected
		}
	}
}
