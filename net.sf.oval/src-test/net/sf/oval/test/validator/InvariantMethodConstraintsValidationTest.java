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
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.ValidationFailedException;

/**
 * @author Sebastian Thomschke
 */
public class InvariantMethodConstraintsValidationTest extends TestCase
{
	public static class TestEntity
	{
		public String name;

		@IsInvariant
		@NotNull(message = "NOT_NULL")
		@Length(max = 4, message = "LENGTH")
		public String getName()
		{
			return name;
		}
	}

	public static class TestEntityInvalidConfig extends TestEntity
	{
		/**
		 * the @NotNull annotation should lead to a warning by the ApiUsageAuditor
		 * because @IsInvariant is missing
		 */
		@NotNull
		public String getSomething()
		{
			return null;
		}

		/**
		 * the @NotNull annotation should lead to a warning by the ApiUsageAuditor
		 */
		@NotNull
		public String doSomething(String value)
		{
			return null;
		}
	}

	public void testMethodReturnValueConstraintValidation()
	{
		final Validator validator = new Validator();
		
		{
			final TestEntity t = new TestEntity();

			List<ConstraintViolation> violations = validator.validate(t);
			assertTrue(violations.size() == 1);
			assertTrue(violations.get(0).getMessage().equals("NOT_NULL"));

			t.name = "wqerwqer";
			violations = validator.validate(t);
			assertTrue(violations.size() == 1);
			assertTrue(violations.get(0).getMessage().equals("LENGTH"));
		}

		{
			final TestEntityInvalidConfig t = new TestEntityInvalidConfig();

			try
			{
				validator.validate(t);
				fail();
			}
			catch (ValidationFailedException e)
			{
				assert (e.getCause() instanceof InvalidConfigurationException);
			}
		}
	}
}
