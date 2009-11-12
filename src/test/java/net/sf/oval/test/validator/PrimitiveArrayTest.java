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
import net.sf.oval.constraint.Max;
import net.sf.oval.constraint.MaxSize;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class PrimitiveArrayTest extends TestCase
{
	public class Account
	{
		@MinSize(value = 1, message = "MIN_SIZE")
		@MaxSize(value = 4, message = "MAX_SIZE")
		@Max(value = 10, message = "MAX")
		@NotNull(message = "NOT_NULL")
		public int[] items = new int[]{};

	}

	public void testPrimitiveArray()
	{
		Validator validator = new Validator();
		Account account = new Account();

		// test min size
		List<ConstraintViolation> violations = validator.validate(account);
		assertEquals(1, violations.size());
		assertEquals("MIN_SIZE", violations.get(0).getMessage());

		// test valid
		account.items = new int[]{1};
		violations = validator.validate(account);
		assertEquals(0, violations.size());

		// test max size
		account.items = new int[]{1, 2, 3, 4, 5};
		violations = validator.validate(account);
		assertEquals(1, violations.size());
		assertEquals("MAX_SIZE", violations.get(0).getMessage());

		// test attribute not null
		account.items = null;
		violations = validator.validate(account);
		assertEquals(1, violations.size());
		assertEquals("NOT_NULL", violations.get(0).getMessage());

		// test elements max
		account.items = new int[]{1, 100};
		violations = validator.validate(account);
		assertEquals(1, violations.size());
		assertEquals("MAX", violations.get(0).getMessage());
	}
}
