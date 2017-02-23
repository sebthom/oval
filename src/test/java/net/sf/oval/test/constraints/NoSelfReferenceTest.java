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

import net.sf.oval.constraint.NoSelfReferenceCheck;

/**
 * @author Sebastian Thomschke
 */
public class NoSelfReferenceTest extends AbstractContraintsTest {
    public void testNoSelfReference() {
        final NoSelfReferenceCheck check = new NoSelfReferenceCheck();
        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        assertTrue(check.isSatisfied(this, null, null, null));
        assertFalse(check.isSatisfied(this, this, null, null));
        assertTrue(check.isSatisfied(this, "bla", null, null));
    }
}
