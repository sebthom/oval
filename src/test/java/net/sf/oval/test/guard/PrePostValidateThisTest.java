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
import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.PostValidateThis;
import net.sf.oval.guard.PreValidateThis;

/**
 * @author Sebastian Thomschke
 */
public class PrePostValidateThisTest extends TestCase {

    @Guarded(applyFieldConstraintsToSetters = true, checkInvariants = false)
    public static class TestEntity {

        private boolean limitLength = false;

        @NotNull(message = "NOT_NULL")
        @MaxLength(value = 4, message = "TOO_LONG", when = "javascript:_this.limitLength == true")
        protected String name;

        public TestEntity() {
            // do nothing
        }

        @PostValidateThis
        public TestEntity(final String name, final boolean limitLength) {
            this.name = name;
            this.limitLength = limitLength;
        }

        @PreValidateThis
        public String getName() {
            return name;
        }

        public boolean isLimitLength() {
            return limitLength;
        }

        public void setName(final String name) {
            this.name = name;
        }

        @PostValidateThis
        public void setNameWithPostValidation(final String name) {
            this.name = name;
        }
    }

    @SuppressWarnings("unused")
    public void testConstructorValidation() {
        try {
            new TestEntity(null, false);

            fail();
        } catch (final ConstraintsViolatedException e) {
            final ConstraintViolation[] violations = e.getConstraintViolations();
            assertNotNull(violations);
            assertEquals(1, violations.length);
            assertEquals("NOT_NULL", violations[0].getMessage());
            assertTrue(violations[0].getContext() instanceof FieldContext);
        }

        new TestEntity("as-long-as-I-what-if_limit-is-not-enabled", false);
        new TestEntity("OK", true);
        try {
            new TestEntity("too-long-when-limit-is-enabled", true);
            fail();
        } catch (final ConstraintsViolatedException e) {
            final ConstraintViolation[] violations = e.getConstraintViolations();
            assertNotNull(violations);
            assertEquals(1, violations.length);
            assertEquals("TOO_LONG", violations[0].getMessage());
            assertTrue(violations[0].getContext() instanceof FieldContext);
        }
    }

    public void testMethodValidation() {
        final TestEntity t = new TestEntity();

        try {
            t.getName();
            fail();
        } catch (final ConstraintsViolatedException e) {
            final ConstraintViolation[] violations = e.getConstraintViolations();
            assertNotNull(violations);
            assertTrue(violations.length > 0);
            assertEquals("NOT_NULL", violations[0].getMessage());
            assertTrue(violations[0].getContext() instanceof FieldContext);
        }

        t.setName("the name");
        assertNotNull(t.getName());

        try {
            t.setName(null);
            fail();
        } catch (final ConstraintsViolatedException e) {
            final ConstraintViolation[] violations = e.getConstraintViolations();
            assertNotNull(violations);
            assertTrue(violations.length > 0);
            assertEquals("NOT_NULL", violations[0].getMessage());
            assertTrue(violations[0].getContext() instanceof MethodParameterContext);
        }

        assertNotNull(t.getName());

        t.setNameWithPostValidation("as-long-as-I-what-if_limit-is-not-enabled");
        t.limitLength = true;
        t.setNameWithPostValidation("OK");
        try {
            t.setNameWithPostValidation("too-long-when-limit-is-enabled");
            fail();
        } catch (final ConstraintsViolatedException e) {
            final ConstraintViolation[] violations = e.getConstraintViolations();
            assertNotNull(violations);
            assertEquals(1, violations.length);
            assertEquals("TOO_LONG", violations[0].getMessage());
            assertTrue(violations[0].getContext() instanceof FieldContext);
        }
    }

    public void testMethodValidationInProbeMode() {
        final TestEntity t = new TestEntity();

        TestGuardAspect.aspectOf().getGuard().enableProbeMode(t);

        final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
        TestGuardAspect.aspectOf().getGuard().addListener(va, t);

        // test non-getter precondition failed
        t.getName();
        assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
        assertTrue(va.getConstraintViolations().size() == 1);
        assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
        va.clear();

        t.setName(null);
        assertTrue(va.getConstraintsViolatedExceptions().size() == 1);
        assertTrue(va.getConstraintViolations().size() == 1);
        assertTrue(va.getConstraintViolations().get(0).getMessage().equals("NOT_NULL"));
        va.clear();

        // test post-condition ignored even if pre-conditions satisfied
        t.setNameWithPostValidation(null);
        assertTrue(va.getConstraintsViolatedExceptions().size() == 0);

        // test setter
        t.setName("the name");
        assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
        assertTrue(va.getConstraintViolations().size() == 0);

        // test getter returns null because we are in probe mode
        t.name = "the name";
        assertNull(t.getName());
        assertTrue(va.getConstraintsViolatedExceptions().size() == 0);
        assertTrue(va.getConstraintViolations().size() == 0);
    }
}
