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

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class StaticFieldsAndGettersTest extends TestCase
{
	protected static class TestEntity
	{
		@NotNull
		public static String staticA;

		public static String staticB;

		@NotNull
		public String nonstaticA;

		public String nonstaticB;

		/**
		 * @return the staticB
		 */
		@IsInvariant
		@NotNull
		public static String getStaticB()
		{
			return staticB;
		}

		/**
		 * @return the nonstaticB
		 */
		@IsInvariant
		@NotNull
		public String getNonstaticB()
		{
			return nonstaticB;
		}
	}

	public void testStaticValidation()
	{
		final Validator validator = new Validator();

		TestEntity.staticA = null;
		TestEntity.staticB = null;

		// test that only static fields are validated
		List<ConstraintViolation> violations = validator.validate(TestEntity.class);
		assertTrue(violations.size() == 2);

		TestEntity.staticA = "";
		TestEntity.staticB = "";

		violations = validator.validate(TestEntity.class);
		assertTrue(violations.size() == 0);
	}

	public void testNonstaticValidation()
	{
		final Validator validator = new Validator();

		TestEntity.staticA = null;
		TestEntity.staticB = null;

		// test that only non static fields are validated
		final TestEntity t = new TestEntity();
		List<ConstraintViolation> violations = validator.validate(t);
		assertTrue(violations.size() == 2);

		t.nonstaticA = "";
		t.nonstaticB = "";

		violations = validator.validate(t);
		assertTrue(violations.size() == 0);
	}
}
