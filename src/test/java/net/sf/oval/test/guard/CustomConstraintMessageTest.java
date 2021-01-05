/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.constraint.Range;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class CustomConstraintMessageTest {

   @Guarded(applyFieldConstraintsToSetters = true)
   protected static class TestEntity {
      @Range(min = 2, max = 4, message = "An amount of {invalidValue} in not in the allowed range ({min}-{max})")
      private int amount = 2;

      @NotNull(message = CUSTOM_ERROR_MESSAGE)
      private String name = "";

      public int getAmount() {
         return amount;
      }

      public String getName() {
         return name;
      }

      public void setAmount(final int amount) {
         this.amount = amount;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   private static final String CUSTOM_ERROR_MESSAGE = "The property [name] cannot be null!";
   private static final String EXPECTED_RANGE_MESSAGE = "An amount of 5 in not in the allowed range (2.0-4.0)";

   /**
    * check that custom messages are used correctly
    */
   @Test
   public void testCustomConstraintMessage() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestEntity e = new TestEntity();

      try {
         e.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);

         if (!CUSTOM_ERROR_MESSAGE.equals(violations[0].getMessage())) {
            fail("The returned error message <" + violations[0].getMessage() + "> does not equal the specified custom error message <" + CUSTOM_ERROR_MESSAGE
               + ">");
         }
      }

      try {
         e.setAmount(5);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);

         if (!EXPECTED_RANGE_MESSAGE.equals(violations[0].getMessage())) {
            fail("The returned error message <" + violations[0].getMessage() + "> does not equal the specified custom error message <" + EXPECTED_RANGE_MESSAGE
               + ">");
         }
      }
   }
}
