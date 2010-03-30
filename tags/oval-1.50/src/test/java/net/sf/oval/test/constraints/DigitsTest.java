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

import net.sf.oval.constraint.DigitsCheck;

/**
 * @author Sebastian Thomschke
 */
public class DigitsTest extends AbstractContraintsTest
{
	public void testDigits()
	{
		final DigitsCheck check = new DigitsCheck();
		super.testCheck(check);
		check.setMaxFraction(2);
		check.setMaxInteger(2);
		assertTrue(check.isSatisfied(null, null, null, null));
		assertTrue(check.isSatisfied(null, "12", null, null));
		assertTrue(check.isSatisfied(null, 12, null, null));
		assertFalse(check.isSatisfied(null, "123", null, null));
		assertFalse(check.isSatisfied(null, 123, null, null));
		assertTrue(check.isSatisfied(null, "12.12", null, null));
		assertTrue(check.isSatisfied(null, 12.12, null, null));
		assertFalse(check.isSatisfied(null, "12.123", null, null));
		assertFalse(check.isSatisfied(null, 12.123, null, null));
	}
}
