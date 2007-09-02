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

import java.math.BigDecimal;
import java.math.BigInteger;

import net.sf.oval.constraint.MinCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinTest extends AbstractContraintsTest
{
	public void testMax()
	{
		final MinCheck check = new MinCheck();
		super.testCheck(check);
		assertTrue(check.isSatisfied(null, null, null, null));

		check.setMin(40);
		assertEquals(40, check.getMin());

		assertTrue(check.isSatisfied(null, "40", null, null));
		assertTrue(check.isSatisfied(null, 40, null, null));
		assertTrue(check.isSatisfied(null, (byte) 40, null, null));
		assertTrue(check.isSatisfied(null, (short) 40, null, null));
		assertTrue(check.isSatisfied(null, (float) 40.0, null, null));
		assertTrue(check.isSatisfied(null, 40.0, null, null));
		assertTrue(check.isSatisfied(null, BigDecimal.valueOf(40), null, null));
		assertTrue(check.isSatisfied(null, BigDecimal.valueOf(40.0), null, null));
		assertTrue(check.isSatisfied(null, BigInteger.valueOf(40), null, null));

		assertTrue(check.isSatisfied(null, "50", null, null));
		assertTrue(check.isSatisfied(null, 50, null, null));
		assertTrue(check.isSatisfied(null, (byte) 50, null, null));
		assertTrue(check.isSatisfied(null, (short) 50, null, null));
		assertTrue(check.isSatisfied(null, (float) 50.0, null, null));
		assertTrue(check.isSatisfied(null, 50.0, null, null));
		assertTrue(check.isSatisfied(null, BigDecimal.valueOf(50), null, null));
		assertTrue(check.isSatisfied(null, BigDecimal.valueOf(50.0), null, null));
		assertTrue(check.isSatisfied(null, BigInteger.valueOf(50), null, null));

		assertFalse(check.isSatisfied(null, "20", null, null));
		assertFalse(check.isSatisfied(null, 20, null, null));
		assertFalse(check.isSatisfied(null, (byte) 20, null, null));
		assertFalse(check.isSatisfied(null, (short) 20, null, null));
		assertFalse(check.isSatisfied(null, (float) 20.0, null, null));
		assertFalse(check.isSatisfied(null, 20.0, null, null));
		assertFalse(check.isSatisfied(null, BigDecimal.valueOf(20), null, null));
		assertFalse(check.isSatisfied(null, BigDecimal.valueOf(20.0), null, null));
		assertFalse(check.isSatisfied(null, BigInteger.valueOf(20), null, null));

		assertFalse(check.isSatisfied(null, "", null, null));
		assertFalse(check.isSatisfied(null, "sdfQ", null, null));
	}
}
