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
