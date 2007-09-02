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

import net.sf.oval.constraint.MinLengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinLengthTest extends AbstractContraintsTest
{
	public void testMinLength()
	{
		final MinLengthCheck check = new MinLengthCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setMin(3);
		assertEquals(3, check.getMin());

		assertTrue(check.isSatisfied(null, "1234", null, null));
		assertFalse(check.isSatisfied(null, "12", null, null));
		assertFalse(check.isSatisfied(null, "", null, null));
	}
}
