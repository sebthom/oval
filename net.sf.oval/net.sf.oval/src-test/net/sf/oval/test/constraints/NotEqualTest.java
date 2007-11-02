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
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.NotEqualCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotEqualTest extends AbstractContraintsTest
{
	public void testNotEqual()
	{
		final NotEqualCheck check = new NotEqualCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setTestString("TEST");
		check.setIgnoreCase(false);
		assertTrue(check.isSatisfied(null, 10, null, null));
		assertTrue(check.isSatisfied(null, "", null, null));
		assertTrue(check.isSatisfied(null, "test", null, null));
		assertFalse(check.isSatisfied(null, "TEST", null, null));

		check.setIgnoreCase(true);
		assertFalse(check.isSatisfied(null, "test", null, null));
		assertFalse(check.isSatisfied(null, "TEST", null, null));
	}
}
