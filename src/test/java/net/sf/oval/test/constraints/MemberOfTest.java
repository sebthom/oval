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

import net.sf.oval.constraint.MemberOfCheck;

/**
 * @author Sebastian Thomschke
 */
public class MemberOfTest extends AbstractContraintsTest {
    public void testMemberOf() {
        final MemberOfCheck check = new MemberOfCheck();
        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        check.setMembers("10", "false", "TRUE");
        check.setIgnoreCase(false);
        assertTrue(check.isSatisfied(null, 10, null, null));
        assertTrue(check.isSatisfied(null, "10", null, null));
        assertFalse(check.isSatisfied(null, 10.0, null, null));
        assertTrue(check.isSatisfied(null, "false", null, null));
        assertTrue(check.isSatisfied(null, false, null, null));
        assertTrue(check.isSatisfied(null, "TRUE", null, null));
        assertFalse(check.isSatisfied(null, true, null, null));

        check.setIgnoreCase(true);
        assertTrue(check.isSatisfied(null, "FALSE", null, null));
        assertTrue(check.isSatisfied(null, false, null, null));
        assertTrue(check.isSatisfied(null, "true", null, null));
        assertTrue(check.isSatisfied(null, true, null, null));
    }
}
