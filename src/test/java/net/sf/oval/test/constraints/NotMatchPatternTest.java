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

import java.util.regex.Pattern;

import net.sf.oval.constraint.NotMatchPatternCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotMatchPatternTest extends AbstractContraintsTest {
    public void testMatchPattern() {
        final NotMatchPatternCheck check = new NotMatchPatternCheck();
        super.testCheck(check);

        check.setPattern("\\d+", 0);
        assertTrue(check.isSatisfied(null, null, null, null));
        assertTrue(check.isSatisfied(null, "", null, null));
        assertFalse(check.isSatisfied(null, "1234", null, null));
        assertTrue(check.isSatisfied(null, "12.34", null, null));
        assertTrue(check.isSatisfied(null, "12,34", null, null));
        assertTrue(check.isSatisfied(null, "foo", null, null));

        check.setPatterns(Pattern.compile("[123]+", 0), Pattern.compile("[abc]+", 0));
        assertTrue(check.isSatisfied(null, null, null, null));
        assertTrue(check.isSatisfied(null, "", null, null));
        assertFalse(check.isSatisfied(null, "12", null, null));
        assertFalse(check.isSatisfied(null, "abc", null, null));
        assertTrue(check.isSatisfied(null, "45", null, null));
        assertTrue(check.isSatisfied(null, "de", null, null));
    }
}