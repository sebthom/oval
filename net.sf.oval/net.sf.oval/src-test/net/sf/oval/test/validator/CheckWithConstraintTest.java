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

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.CheckWith;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.CheckWithCheck.SimpleCheck;

/**
 * @author Sebastian Thomschke
 */
public class CheckWithConstraintTest extends TestCase
{

	private static class TestEntity1
	{
		private static class NameCheck1 implements SimpleCheck
		{
			private static final long serialVersionUID = 1L;

			public boolean isSatisfied(Object validatedObject, Object value)
			{
				String name = (String) value;

				if (name == null) return false;
				if (name.length() == 0) return false;
				if (name.length() > 4) return false;
				return true;
			}
		}

		@CheckWith(value = NameCheck1.class, ignoreIfNull = false)
		public String name;
	}

	private static class TestEntity2
	{
		private static class NameCheck2 implements SimpleCheck
		{
			private static final long serialVersionUID = 1L;

			public boolean isSatisfied(Object validatedObject, Object value)
			{
				return ((TestEntity2) validatedObject).isValidName((String) value);
			}
		}

		@CheckWith(value = NameCheck2.class)
		@NotNull
		public String name;

		private boolean isValidName(String name)
		{
			if (name.length() == 0) return false;
			if (name.length() > 4) return false;
			return true;
		}
	}

	public void testCheckWith1()
	{
		final Validator validator = new Validator();

		final TestEntity1 t = new TestEntity1();

		List<ConstraintViolation> violations;

		violations = validator.validate(t);
		assertTrue(violations.size() == 1);

		t.name = "";
		violations = validator.validate(t);
		assertTrue(violations.size() == 1);

		t.name = "12345";
		violations = validator.validate(t);
		assertTrue(violations.size() == 1);

		t.name = "1234";
		violations = validator.validate(t);
		assertTrue(violations.size() == 0);
	}

	public void testCheckWith2()
	{
		final Validator validator = new Validator();

		final TestEntity2 t = new TestEntity2();

		List<ConstraintViolation> violations;

		violations = validator.validate(t);
		assertTrue(violations.size() == 1);

		t.name = "";
		violations = validator.validate(t);
		assertTrue(violations.size() == 1);

		t.name = "12345";
		violations = validator.validate(t);
		assertTrue(violations.size() == 1);

		t.name = "1234";
		violations = validator.validate(t);
		assertTrue(violations.size() == 0);
	}
}
