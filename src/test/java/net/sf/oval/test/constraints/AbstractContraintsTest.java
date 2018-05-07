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

import junit.framework.TestCase;
import net.sf.oval.Check;
import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public abstract class AbstractContraintsTest extends TestCase {
    protected final Validator validator = new Validator();

    /**
     * Performs basic tests of the check implementation.
     * 
     * @param check
     */
    protected void testCheck(final Check check) {
        check.setMessage("XYZ");
        assertEquals("XYZ", check.getMessage());

        check.setProfiles("p1");
        assertNotNull(check.getProfiles());
        assertEquals(1, check.getProfiles().length);
        assertEquals("p1", check.getProfiles()[0]);

        check.setProfiles((String[]) null);
        assertTrue(check.getProfiles() == null || check.getProfiles().length == 0);
    }
}
