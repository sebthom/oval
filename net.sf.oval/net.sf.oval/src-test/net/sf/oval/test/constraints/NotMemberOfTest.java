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

import net.sf.oval.constraint.NotMemberOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotMemberOfTest extends AbstractContraintsTest
{
	public void testNotMemberOf()
	{
		final NotMemberOfCheck check = new NotMemberOfCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setMembers("10", "false", "TRUE");
		check.setIgnoreCase(false);
		assertFalse(check.isSatisfied(null, 10, null, null));
		assertFalse(check.isSatisfied(null, "10", null, null));
		assertTrue(check.isSatisfied(null, 10.0, null, null));
		assertFalse(check.isSatisfied(null, "false", null, null));
		assertFalse(check.isSatisfied(null, false, null, null));
		assertFalse(check.isSatisfied(null, "TRUE", null, null));
		assertTrue(check.isSatisfied(null, true, null, null));

		check.setIgnoreCase(true);
		assertFalse(check.isSatisfied(null, "FALSE", null, null));
		assertFalse(check.isSatisfied(null, false, null, null));
		assertFalse(check.isSatisfied(null, "true", null, null));
		assertFalse(check.isSatisfied(null, true, null, null));
	}
}
