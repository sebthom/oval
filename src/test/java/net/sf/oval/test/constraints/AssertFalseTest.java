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

import net.sf.oval.constraint.AssertFalseCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertFalseTest extends AbstractContraintsTest {
    public void testAssertFalse() {
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
