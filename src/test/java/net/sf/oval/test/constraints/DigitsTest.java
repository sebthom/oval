/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2017 Sebastian
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

import net.sf.oval.constraint.DigitsCheck;

/**
 * @author Sebastian Thomschke
 */
public class DigitsTest extends AbstractContraintsTest {
    public void testDigits() {
        final DigitsCheck check = new DigitsCheck();
        super.testCheck(check);
        check.setMaxFraction(2);
        check.setMaxInteger(2);

        assertTrue(check.isSatisfied(null, null, null, null));

        assertTrue(check.isSatisfied(null, 0, null, null));
        assertTrue(check.isSatisfied(null, 0.0, null, null));
        assertTrue(check.isSatisfied(null, 0.00, null, null));
        assertTrue(check.isSatisfied(null, 0.000, null, null)); // returns true since it equals 0.0d
        assertFalse(check.isSatisfied(null, 0.001, null, null));
        assertTrue(check.isSatisfied(null, 000.0, null, null)); // returns true since it equals 0.0d
        assertTrue(check.isSatisfied(null, 12, null, null));
        assertFalse(check.isSatisfied(null, 123, null, null));
        assertTrue(check.isSatisfied(null, 12.12, null, null));
        assertFalse(check.isSatisfied(null, 12.123, null, null));
        assertFalse(check.isSatisfied(null, 123.12, null, null));

        assertTrue(check.isSatisfied(null, "0", null, null));
        assertTrue(check.isSatisfied(null, "0.0", null, null));
        assertTrue(check.isSatisfied(null, "0.00", null, null));
        assertFalse(check.isSatisfied(null, "0.000", null, null)); // returns false since BigDecimal keeps the fraction
        assertFalse(check.isSatisfied(null, "0.001", null, null));
        assertTrue(check.isSatisfied(null, "000.0", null, null)); // TODO returns true since it is automatically converted into a BigDecimal("0.0") => should return false in case of pure strings?
        assertTrue(check.isSatisfied(null, "12", null, null));
        assertFalse(check.isSatisfied(null, "123", null, null));
        assertTrue(check.isSatisfied(null, "12.12", null, null));
        assertFalse(check.isSatisfied(null, "12.123", null, null));
        assertFalse(check.isSatisfied(null, "123.12", null, null));

        assertTrue(check.isSatisfied(null, new BigDecimal("0"), null, null));
        assertTrue(check.isSatisfied(null, new BigDecimal("0.0"), null, null));
        assertTrue(check.isSatisfied(null, new BigDecimal("0.00"), null, null));
        assertFalse(check.isSatisfied(null, new BigDecimal("0.000"), null, null)); // TODO returns false since BigDecimal keeps the fraction => should this return true for BigDecimals ?
        assertFalse(check.isSatisfied(null, new BigDecimal("0.001"), null, null));
        assertTrue(check.isSatisfied(null, new BigDecimal("000.0"), null, null)); // returns true since it is automatically converted into a BigDecimal("0.0")
        assertTrue(check.isSatisfied(null, new BigDecimal("12"), null, null));
        assertFalse(check.isSatisfied(null, new BigDecimal("123"), null, null));
        assertTrue(check.isSatisfied(null, new BigDecimal("12.12"), null, null));
        assertFalse(check.isSatisfied(null, new BigDecimal("12.123"), null, null));
        assertFalse(check.isSatisfied(null, new BigDecimal("123.12"), null, null));

        check.setMaxInteger(13);
        check.setMaxFraction(13);
        assertTrue(check.isSatisfied(null, 1234567890123L, null, null));
        assertFalse(check.isSatisfied(null, 12345678901234L, null, null));
        assertTrue(check.isSatisfied(null, "1234567890123", null, null));
        assertFalse(check.isSatisfied(null, "12345678901234", null, null));
        assertTrue(check.isSatisfied(null, new BigDecimal("1234567890123"), null, null));
        assertFalse(check.isSatisfied(null, new BigDecimal("12345678901234"), null, null));
        assertTrue(check.isSatisfied(null, new BigDecimal("1234567890123.1234567890123"), null, null));
        assertFalse(check.isSatisfied(null, new BigDecimal("12345678901234.12345678901234"), null, null));
    }
}
