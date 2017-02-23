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

import net.sf.oval.constraint.NotEqualToFieldCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotEqualToFieldTest extends AbstractContraintsTest {
    public static class EnrichedEntity extends Entity {
        @SuppressWarnings("hiding")
        protected String password1;

        protected String password1Alternative;

        protected String password2Alternative;
    }

    public static class Entity {
        protected String password1 = "mug";
        protected String password2WhatEver;

        public String getPassword2() {
            return password2WhatEver;
        }

    }

    public void testEqualToField() {
        final NotEqualToFieldCheck check = new NotEqualToFieldCheck();
        super.testCheck(check);
        assertTrue(check.isSatisfied(null, null, null, null));

        final EnrichedEntity entity = new EnrichedEntity();
        entity.password1 = "secret";
        entity.password1Alternative = "zecret";

        check.setFieldName("password1");
        check.setUseGetter(false);

        assertTrue(check.isSatisfied(entity, entity.password1Alternative, null, null));
        entity.password1Alternative = "secret";
        assertFalse(check.isSatisfied(entity, entity.password1Alternative, null, null));

        entity.password2WhatEver = "secret";
        entity.password2Alternative = "zecret";

        check.setFieldName("password2");
        check.setUseGetter(true);

        assertTrue(check.isSatisfied(entity, entity.password2Alternative, null, null));
        entity.password2Alternative = "secret";
        assertFalse(check.isSatisfied(entity, entity.password2Alternative, null, null));

    }
}
