/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.PostValidateThis;

/**
 * @author Sebastian Thomschke
 */
public class CheckInvariantsTest {

   @Guarded(checkInvariants = true)
   public static class TestEntity {

      private String fieldA;

      @NotNull(when = "javascript:_this.fieldA != null", message = "NOT_NULL")
      private String fieldB;

      @PostValidateThis
      public TestEntity(final String fieldA, final String fieldB) {
         this.fieldA = fieldA;
         this.fieldB = fieldB;
      }

      public String getFieldA() {
         return fieldA;
      }

      public String getFieldB() {
         return fieldB;
      }

      public void setFieldA(final String fieldA) {
         this.fieldA = fieldA;
      }

      public void setFieldB(final String fieldB) {
         this.fieldB = fieldB;
      }
   }

   @Test
   @SuppressWarnings("unused")
   public void testCheckInvariants() {

      try {
         new TestEntity("a", null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }

      new TestEntity(null, null);

      final TestEntity e = new TestEntity("a", "b");
      try {
         e.setFieldB(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }
   }

}
