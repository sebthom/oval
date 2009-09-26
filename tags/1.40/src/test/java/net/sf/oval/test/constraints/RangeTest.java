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
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.RangeCheck;

/**
 * @author Sebastian Thomschke
 */
public class RangeTest extends AbstractContraintsTest
{
	public void testRange()
	{
		final RangeCheck check = new RangeCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setMin(3);
		assertEquals(3.0, check.getMin());

		assertTrue(check.isSatisfied(null, "16", null, null));

		check.setMax(6);
		assertEquals(6.0, check.getMax());

		assertTrue(check.isSatisfied(null, "4", null, null));
		assertFalse(check.isSatisfied(null, "16", null, null));
		assertFalse(check.isSatisfied(null, "2", null, null));
	}
}
