/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintListWithWhenConditionTest extends TestCase
{
	static class Account
	{
		boolean isActive = false;

		@NotNull.List(value = @NotNull, when = "groovy:_this.isActive")
		String password;
	}

	public void testWhenCondition() throws Exception
	{
		final Validator validator = new Validator();
		final Account account = new Account();

		assertTrue(validator.validate(account).isEmpty());

		account.isActive = true;
		assertEquals(1, validator.validate(account).size());
		assertEquals(NotNull.class.getName() + ".violated", validator.validate(account).get(0).getMessageTemplate());
	}

}