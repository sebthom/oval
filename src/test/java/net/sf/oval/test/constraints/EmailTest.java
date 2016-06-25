/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import net.sf.oval.constraint.EmailCheck;

/**
 * @author Sebastian Thomschke
 */
public class EmailTest extends AbstractContraintsTest {
    public void testEmailWithoutPersonalNameAllowed() {
        final EmailCheck check = new EmailCheck();

        check.setAllowPersonalName(false);

        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        assertTrue(check.isSatisfied(null, "testjee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test_jee@yahoo.co", null, null));
        assertTrue(check.isSatisfied(null, "test.jee@yahoo.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "test.jee@yahoo.co.biz", null, null));
        assertTrue(check.isSatisfied(null, "test_jee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test_jee@yahoo.net", null, null));
        assertTrue(check.isSatisfied(null, "user@subname1.subname2.subname3.domainname.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "test.j'ee@yahoo.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "test.j'e.e'@yahoo.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "testj'ee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test&jee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test+jee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test_j.s.@yahoo.com", null, null));

        assertFalse(check.isSatisfied(null, "testjee@@yahoo.com", null, null));
        assertFalse(check.isSatisfied(null, "test_jee#marry@yahoo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee@ yahoo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee  @yahoo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_j ee  @yah oo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee  @yah oo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee @ yahoo.com", null, null));
        assertFalse(check.isSatisfied(null, "user@subname1.subname2.subname3.domainn#ame.co.uk", null, null));

        assertFalse(check.isSatisfied(null, "John <testjee@yahoo.com>", null, null));
        assertFalse(check.isSatisfied(null, "John Doe <testjee@yahoo.com>", null, null));
        assertFalse(check.isSatisfied(null, "John@ <testjee@yahoo.com>", null, null));
    }

    public void testEmailWithPersonalNameAllowed() {
        final EmailCheck check = new EmailCheck();

        check.setAllowPersonalName(true);

        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        assertTrue(check.isSatisfied(null, "testjee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test_jee@yahoo.co", null, null));
        assertTrue(check.isSatisfied(null, "test.jee@yahoo.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "test.jee@yahoo.co.biz", null, null));
        assertTrue(check.isSatisfied(null, "test_jee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test_jee@yahoo.net", null, null));
        assertTrue(check.isSatisfied(null, "user@subname1.subname2.subname3.domainname.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "test.j'ee@yahoo.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "test.j'e.e'@yahoo.co.uk", null, null));
        assertTrue(check.isSatisfied(null, "testj'ee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test&jee@yahoo.com", null, null));
        assertTrue(check.isSatisfied(null, "test_j.s.@yahoo.com", null, null));

        assertFalse(check.isSatisfied(null, "testjee@@yahoo.com", null, null));
        assertFalse(check.isSatisfied(null, "test_jee#marry@yahoo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee@ yahoo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee  @yahoo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_j ee  @yah oo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee  @yah oo.co.uk", null, null));
        assertFalse(check.isSatisfied(null, "test_jee @ yahoo.com", null, null));
        assertFalse(check.isSatisfied(null, "user@subname1.subname2.subname3.domainn#ame.co.uk", null, null));

        assertTrue(check.isSatisfied(null, "John <testjee@yahoo.com>", null, null));
        assertTrue(check.isSatisfied(null, "John Doe <testjee@yahoo.com>", null, null));
        assertFalse(check.isSatisfied(null, "John@ <testjee@yahoo.com>", null, null));
    }
}