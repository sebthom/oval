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

import net.sf.oval.constraint.NotNullCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotNullTest extends AbstractContraintsTest {
    public void testNotNull() {
        final NotNullCheck check = new NotNullCheck();
        super.testCheck(check);
        assertFalse(check.isSatisfied(null, null, null, null));
        assertTrue(check.isSatisfied(null, "bla", null, null));
        assertTrue(check.isSatisfied(null, true, null, null));
        assertTrue(check.isSatisfied(null, 1, null, null));
        assertTrue(check.isSatisfied(null, "", null, null));
        assertTrue(check.isSatisfied(null, ' ', null, null));
        assertTrue(check.isSatisfied(null, " ", null, null));
    }
}
