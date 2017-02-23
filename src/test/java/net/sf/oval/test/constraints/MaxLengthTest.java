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

import net.sf.oval.constraint.MaxLengthCheck;

/**
 * @author Sebastian Thomschke
 */
public class MaxLengthTest extends AbstractContraintsTest {
    public void testMaxLength() {
        final MaxLengthCheck check = new MaxLengthCheck();
        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        check.setMax(5);
        assertEquals(5, check.getMax());

        assertTrue(check.isSatisfied(null, "1234", null, null));
        assertTrue(check.isSatisfied(null, "12", null, null));
        assertTrue(check.isSatisfied(null, "", null, null));
        assertFalse(check.isSatisfied(null, "123456", null, null));
    }
}
