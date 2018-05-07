/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.constraints;

import net.sf.oval.constraint.RangeCheck;

/**
 * @author Sebastian Thomschke
 */
public class RangeTest extends AbstractContraintsTest {
    public void testRange() {
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
