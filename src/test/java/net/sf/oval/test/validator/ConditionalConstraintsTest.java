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
package net.sf.oval.test.validator;

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ConditionalConstraintsTest extends TestCase {
    protected static class TestEntity {
        @NotNull(when = "groovy:_this.lastname!=null", message = "NOT_NULL")
        public String firstname;

        public String lastname;
    }

    public void testConstraintViolationOrder() {
        final TestEntity e = new TestEntity();
        final Validator v = new Validator();
        List<ConstraintViolation> violations = v.validate(e);
        assertEquals(0, violations.size());
        e.lastname = "foo";
        violations = v.validate(e);
        assertEquals(1, violations.size());
        assertEquals("NOT_NULL", violations.get(0).getMessage());
    }
}
