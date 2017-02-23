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

import net.sf.oval.constraint.HasSubstringCheck;

/**
 * @author Sebastian Thomschke
 */
public class HasSubStringTest extends AbstractContraintsTest {
    public void testHasSubString() {
        final HasSubstringCheck check = new HasSubstringCheck();
        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        check.setSubstring("TeSt");
        assertEquals("TeSt", check.getSubstring());

        check.setIgnoreCase(false);
        assertFalse(check.isIgnoreCase());

        assertFalse(check.isSatisfied(null, "bla", null, null));
        assertFalse(check.isSatisfied(null, "test", null, null));
        assertTrue(check.isSatisfied(null, "TeSt", null, null));
        assertTrue(check.isSatisfied(null, "aaaTeStaaaa", null, null));
        assertTrue(check.isSatisfied(null, "TeStaaaa", null, null));
        assertTrue(check.isSatisfied(null, "aaaTeSt", null, null));

        check.setIgnoreCase(true);
        assertTrue(check.isIgnoreCase());

        assertFalse(check.isSatisfied(null, "bla", null, null));
        assertTrue(check.isSatisfied(null, "test", null, null));
        assertTrue(check.isSatisfied(null, "TEst", null, null));
        assertTrue(check.isSatisfied(null, "aaaTesTaaaa", null, null));
        assertTrue(check.isSatisfied(null, "TEstaaaa", null, null));
        assertTrue(check.isSatisfied(null, "aaatESt", null, null));
    }
}
