/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
import net.sf.oval.constraint.ValidateWithMethod;

/**
 * @author Sebastian Thomschke
 */
public class ValidateWithMethodConstraintTest extends TestCase
{
	protected static class BaseEntity
	{
		protected boolean isNameValid(final String name)
		{
			if (name == null) return false;
			if (name.length() == 0) return false;
			if (name.length() > 4) return false;
			return true;
		}
	}

	protected static class TestEntity extends BaseEntity
	{
		@ValidateWithMethod(methodName = "isNameValid", parameterType = String.class, ignoreIfNull = false)
		public String name;
	}

	public void testCheckByMethod()
	{
		final Validator validator = new Validator();

		final TestEntity t = new TestEntity();

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
