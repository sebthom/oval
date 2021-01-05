/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ParameterConstraintsTest {

   @Guarded
   public static class TestEntity {
      @NotNull(message = "NOT_NULL")
      private String name = "";

      /**
       * Constructor 1
       */
      public TestEntity(@NotNull(message = "NOT_NULL") final String name) {
         this.name = name;
      }

      /**
       * Constructor 2
       */
      public TestEntity(final String name, @SuppressWarnings("unused") final int bla) {
         this.name = name;
      }

      public void setName(@NotNull(message = "NOT_NULL") @Length(max = 4, message = "LENGTH") final String name) {
         this.name = name;
      }
   }

   @Test
   @SuppressWarnings("unused")
   public void testConstructorParameterConstraints() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      /*
       * Testing Constructor 1
       * parameter constraint
       */
      try {
         new TestEntity(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(ConstructorParameterContext.class);
      }

      new TestEntity("test");

      /*
       * Testing Constructor 2
       * invariant constraint
       */
      try {
         new TestEntity(null, 100);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }
   }

   @Test
   public void testMethodParameters() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      try {
         final TestEntity t1 = new TestEntity("");
         t1.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations.length > 0).isTrue();
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
      }

      try {
         final TestEntity t1 = new TestEntity("");
         t1.setName("12345678");
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations.length > 0).isTrue();
         assertThat(violations[0].getMessage()).isEqualTo("LENGTH");
      }
   }

   @Test
   public void testMethodParametersInProbeMode() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestEntity entity = new TestEntity("");

      guard.enableProbeMode(entity);

      final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
      guard.addListener(va, entity);

      entity.setName(null);
      entity.setName("12345678");
      final List<ConstraintViolation> violations = va.getConstraintViolations();
      assertThat(violations).hasSize(2);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");
      assertThat(violations.get(1).getMessage()).isEqualTo("LENGTH");

      guard.removeListener(va, entity);
   }
}
