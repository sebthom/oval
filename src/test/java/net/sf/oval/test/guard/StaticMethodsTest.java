/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import junit.framework.TestCase;
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class StaticMethodsTest extends TestCase {
    @Guarded
    private static class TestEntity {
        public static void doSomethingPost() {
            //
        }

        public static void doSomethingPre() {
            //
        }

        public static void setValue(@AssertFieldConstraints final String value) {
            TestEntity.value = value;
        }

        @NotNull(message = "NULL")
        public static String value;
    }

    public void testPostValidateThis() throws Exception {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        TestEntity.value = null;

        try {
            TestEntity.doSomethingPost();
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertTrue(ex.getConstraintViolations().length == 1);
            assertTrue(ex.getConstraintViolations()[0].getMessage().equals("NULL"));
        }

        TestEntity.value = "";
        TestEntity.doSomethingPost();
    }

    public void testPreValidateThis() throws Exception {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        TestEntity.value = null;

        try {
            TestEntity.doSomethingPre();
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertTrue(ex.getConstraintViolations().length == 1);
            assertTrue(ex.getConstraintViolations()[0].getMessage().equals("NULL"));
        }

        TestEntity.value = "";
        TestEntity.doSomethingPre();
    }

    public void testSetterValidation() throws Exception {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        try {
            TestEntity.setValue(null);
            fail();
        } catch (final ConstraintsViolatedException ex) {
            assertTrue(ex.getConstraintViolations().length == 1);
            assertTrue(ex.getConstraintViolations()[0].getMessage().equals("NULL"));
        }
    }
}
