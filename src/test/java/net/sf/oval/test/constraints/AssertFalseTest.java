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
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.AssertFalseCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertFalseTest extends AbstractContraintsTest
{
	public void testAssertFalse()
	{
		final AssertFalseCheck check = new AssertFalseCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		assertTrue(check.isSatisfied(null, false, null, null));
		assertFalse(check.isSatisfied(null, true, null, null));
		assertTrue(check.isSatisfied(null, Boolean.FALSE, null, null));
		assertFalse(check.isSatisfied(null, Boolean.TRUE, null, null));
		assertFalse(check.isSatisfied(null, "true", null, null));
		assertTrue(check.isSatisfied(null, "bla", null, null));
	}
}
