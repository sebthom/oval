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
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Range;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class CustomConstraintMessageTest extends TestCase {
    @Guarded(applyFieldConstraintsToSetters = true)
    protected static class TestEntity {
        @Range(min = 2, max = 4, message = "An amount of {invalidValue} in not in the allowed range ({min}-{max})")
        private int amount = 2;

        @NotNull(message = CUSTOM_ERROR_MESSAGE)
        private String name = "";

        /**
         * @return the amount
         */
        public int getAmount() {
            return amount;
        }

        /**
         * @return the name
         */
        public String getName() {
            return name;
        }

        /**
         * @param amount the amount to set
         */
        public void setAmount(final int amount) {
            this.amount = amount;
        }

        /**
         * @param name the name to set
         */
        public void setName(final String name) {
            this.name = name;
        }
    }

    private static final String CUSTOM_ERROR_MESSAGE = "The property [name] cannot be null!";
    private static final String EXPECTED_RANGE_MESSAGE = "An amount of 5 in not in the allowed range (2.0-4.0)";

    /**
     * check that custom messages are used correctly
     */
    public void testCustomConstraintMessage() {
        final Guard guard = new Guard();
        TestGuardAspect.aspectOf().setGuard(guard);

        final TestEntity e = new TestEntity();

        try {
            e.setName(null);
            fail();
        } catch (final ConstraintsViolatedException ex) {
            final ConstraintViolation[] violations = ex.getConstraintViolations();
            assertNotNull(violations);
            assertEquals(1, violations.length);

            if (!CUSTOM_ERROR_MESSAGE.equals(violations[0].getMessage())) {
                fail("The returned error message <" + violations[0].getMessage() + "> does not equal the specified custom error message <"
                        + CUSTOM_ERROR_MESSAGE + ">");
            }
        }

        try {
            e.setAmount(5);
            fail();
        } catch (final ConstraintsViolatedException ex) {
            final ConstraintViolation[] violations = ex.getConstraintViolations();
            assertNotNull(violations);
            assertEquals(1, violations.length);

            if (!EXPECTED_RANGE_MESSAGE.equals(violations[0].getMessage())) {
                fail("The returned error message <" + violations[0].getMessage() + "> does not equal the specified custom error message <"
                        + EXPECTED_RANGE_MESSAGE + ">");
            }
        }
    }
}
