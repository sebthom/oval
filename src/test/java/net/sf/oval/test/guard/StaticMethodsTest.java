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

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class StaticMethodsTest {

   @Guarded
   private static class TestEntity {

      @NotNull(message = "NULL")
      public static String value;

      public static void doSomethingPost() {
         //
      }

      public static void doSomethingPre() {
         //
      }

      public static void setValue(@AssertFieldConstraints final String value) {
         TestEntity.value = value;
      }
   }

   @Test
   public void testPostValidateThis() throws Exception {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      TestEntity.value = null;

      try {
         TestEntity.doSomethingPost();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()).hasSize(1);
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("NULL");
      }

      TestEntity.value = "";
      TestEntity.doSomethingPost();
   }

   @Test
   public void testPreValidateThis() throws Exception {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      TestEntity.value = null;

      try {
         TestEntity.doSomethingPre();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()).hasSize(1);
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("NULL");
      }

      TestEntity.value = "";
      TestEntity.doSomethingPre();
   }

   @Test
   public void testSetterValidation() throws Exception {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      try {
         TestEntity.setValue(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getConstraintViolations()).hasSize(1);
         assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("NULL");
      }
   }
}
