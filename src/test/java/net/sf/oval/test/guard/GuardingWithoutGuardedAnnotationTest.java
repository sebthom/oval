/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;

/**
 * @author Sebastian Thomschke
 */
public class GuardingWithoutGuardedAnnotationTest {

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
      guard.setInvariantsEnabled(TestEntity.class, true);
      GuardingWithoutGuardedAnnotationAspect.aspectOf().setGuard(guard);

      /*
       * Testing Constructor 1
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
       */
      try {
         new TestEntity(null, 100);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class); // invariant check on name fails
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   @Test
   public void testMethodParameterConstraints() {
      final Guard guard = new Guard();
      guard.setInvariantsEnabled(TestEntity.class, true);
      GuardingWithoutGuardedAnnotationAspect.aspectOf().setGuard(guard);

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
}
