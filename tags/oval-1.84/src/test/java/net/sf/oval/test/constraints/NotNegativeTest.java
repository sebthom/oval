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

import java.math.BigDecimal;

import net.sf.oval.constraint.NotNegativeCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotNegativeTest extends AbstractContraintsTest
{
	public void testNotNegative()
	{
		final NotNegativeCheck check = new NotNegativeCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		assertTrue(check.isSatisfied(null, 0, null, null));
		assertTrue(check.isSatisfied(null, 0.0, null, null));
		assertTrue(check.isSatisfied(null, 1, null, null));
		assertTrue(check.isSatisfied(null, 1.0, null, null));
		assertTrue(check.isSatisfied(null, "0", null, null));
		assertTrue(check.isSatisfied(null, "0.0", null, null));
		assertTrue(check.isSatisfied(null, "1", null, null));
		assertTrue(check.isSatisfied(null, "1.0", null, null));
		assertFalse(check.isSatisfied(null, "-1", null, null));
		assertFalse(check.isSatisfied(null, "-1.0", null, null));
		assertFalse(check.isSatisfied(null, false, null, null));
		assertFalse(check.isSatisfied(null, true, null, null));
		assertFalse(check.isSatisfied(null, new BigDecimal(-1), null, null));
		assertTrue(check.isSatisfied(null, new BigDecimal(0), null, null));
		assertTrue(check.isSatisfied(null, new BigDecimal(1), null, null));
	}
}
