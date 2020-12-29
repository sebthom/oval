/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Field;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 */
public class ValidatorAssertValidTest {
   protected static class TestEntity {
      @NotNull(message = "NOT_NULL")
      public String name;

      @NotNull(message = "NOT_NULL")
      public Integer value;
   }

   @Test
   public void testValidatorAssert() throws Exception {
      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      try {
         v.assertValid(e);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).hasSize(2);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
         assertThat(violations[1].getMessage()).isEqualTo("NOT_NULL");
      }

      e.name = "asdads";
      e.value = 5;
      v.assertValid(e);
   }

   @Test
   public void testValidatorAssertField() throws Exception {
      final Field f = TestEntity.class.getField("name");

      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      try {
         v.assertValidFieldValue(e, f, null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
      }

      v.assertValidFieldValue(e, f, "test");
   }
}
