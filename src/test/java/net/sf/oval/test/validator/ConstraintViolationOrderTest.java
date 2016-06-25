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
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.EqualToField;
import net.sf.oval.constraint.HasSubstring;
import net.sf.oval.constraint.MaxLength;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintViolationOrderTest extends TestCase {
    protected static class TestEntity {
        @MaxLength(value = 5, message = "VIO1")
        @EqualToField(value = "value", message = "VIO2")
        @HasSubstring(value = "foo", message = "VIO3")
        public String name = "12345678";

        public String value = "123";
    }

    public void testConstraintViolationOrder() {
        final TestEntity e = new TestEntity();
        final Validator v = new Validator();
        final List<ConstraintViolation> violations = v.validate(e);
        assertEquals(3, violations.size());
        assertEquals("VIO1", violations.get(0).getMessage());
        assertEquals("VIO2", violations.get(1).getMessage());
        assertEquals("VIO3", violations.get(2).getMessage());
    }
}
