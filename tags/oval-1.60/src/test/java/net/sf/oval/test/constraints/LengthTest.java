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

import net.sf.oval.constraint.LengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class LengthTest extends AbstractContraintsTest
{
	public void testLength()
	{
		final LengthCheck check = new LengthCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setMax(5);
		check.setMin(3);
		assertEquals(5, check.getMax());
		assertEquals(3, check.getMin());

		assertTrue(check.isSatisfied(null, "1234", null, null));
		assertFalse(check.isSatisfied(null, "12", null, null));
		assertFalse(check.isSatisfied(null, "", null, null));
		assertFalse(check.isSatisfied(null, "123456", null, null));
	}
}
