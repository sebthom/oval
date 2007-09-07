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

import java.lang.reflect.Field;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 */
public class ValidatorAssertTest extends TestCase
{
	private class TestEntity
	{
		@NotNull(message = "NOT_NULL")
		public String name;

		@NotNull(message = "NOT_NULL")
		public Integer value;

	}

	public void testValidatorAssert() throws Exception
	{
		TestEntity e = new TestEntity();
		Validator v = new Validator();
		try
		{
			v.assertValid(e);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertEquals(2, violations.length);
			assertEquals("NOT_NULL", violations[0].getMessage());
			assertEquals("NOT_NULL", violations[1].getMessage());
		}

		e.name = "asdads";
		e.value = 5;
		v.assertValid(e);
	}

	public void testValidatorAssertField() throws Exception
	{
		Field f = TestEntity.class.getField("name");

		TestEntity e = new TestEntity();
		Validator v = new Validator();
		try
		{
			v.assertValidFieldValue(e, f, null);
			fail();
		}
		catch (ConstraintsViolatedException ex)
		{
			ConstraintViolation[] violations = ex.getConstraintViolations();
			assertEquals(1, violations.length);
			assertEquals("NOT_NULL", violations[0].getMessage());
		}

		v.assertValidFieldValue(e, f, "test");
	}
}
