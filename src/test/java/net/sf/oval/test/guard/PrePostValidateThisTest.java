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
public class PrePostValidateThisTest {

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

   @Test
   @SuppressWarnings("unused")
   public void testConstructorValidation() {
      try {
         new TestEntity(null, false);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }

      new TestEntity("as-long-as-I-want-if_limit-is-not-enabled", false);
      new TestEntity("OK", true);
      try {
         new TestEntity("too-long-when-limit-is-enabled", true);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("TOO_LONG");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }
   }

   @Test
   public void testMethodValidation() {
      final TestEntity t = new TestEntity();

      try {
         t.getName();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations.length > 0).isTrue();
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }

      t.setName("the name");
      assertThat(t.getName()).isNotNull();

      try {
         t.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations.length > 0).isTrue();
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[0].getContext()).isInstanceOf(MethodParameterContext.class);
      }

      assertThat(t.getName()).isNotNull();

      t.setNameWithPostValidation("as-long-as-I-want-if_limit-is-not-enabled");
      t.limitLength = true;
      t.setNameWithPostValidation("OK");
      try {
         t.setNameWithPostValidation("too-long-when-limit-is-enabled");
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).isNotNull();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("TOO_LONG");
         assertThat(violations[0].getContext()).isInstanceOf(FieldContext.class);
      }
   }

   @Test
   public void testMethodValidationInProbeMode() {
      final TestEntity t = new TestEntity();

      TestGuardAspect.aspectOf().getGuard().enableProbeMode(t);

      final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
      TestGuardAspect.aspectOf().getGuard().addListener(va, t);

      // test non-getter precondition failed
      t.getName();
      assertThat(va.getConstraintsViolatedExceptions()).hasSize(1);
      assertThat(va.getConstraintViolations()).hasSize(1);
      assertThat(va.getConstraintViolations().get(0).getMessage()).isEqualTo("NOT_NULL");
      va.clear();

      t.setName(null);
      assertThat(va.getConstraintsViolatedExceptions()).hasSize(1);
      assertThat(va.getConstraintViolations()).hasSize(1);
      assertThat(va.getConstraintViolations().get(0).getMessage()).isEqualTo("NOT_NULL");
      va.clear();

      // test post-condition ignored even if pre-conditions satisfied
      t.setNameWithPostValidation(null);
      assertThat(va.getConstraintsViolatedExceptions()).isEmpty();

      // test setter
      t.setName("the name");
      assertThat(va.getConstraintsViolatedExceptions()).isEmpty();
      assertThat(va.getConstraintViolations()).isEmpty();

      // test getter returns null because we are in probe mode
      t.name = "the name";
      assertThat(t.getName()).isNull();
      assertThat(va.getConstraintsViolatedExceptions()).isEmpty();
      assertThat(va.getConstraintViolations()).isEmpty();
   }
}
