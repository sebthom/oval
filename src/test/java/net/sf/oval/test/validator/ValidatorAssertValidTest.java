/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import java.lang.reflect.Field;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;

/**
 * @author Sebastian Thomschke
 */
public class ValidatorAssertValidTest extends TestCase {
   protected static class TestEntity {
      @NotNull(message = "NOT_NULL")
      public String name;

      @NotNull(message = "NOT_NULL")
      public Integer value;
   }

   public void testValidatorAssert() throws Exception {
      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      try {
         v.assertValid(e);
         fail();
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertEquals(2, violations.length);
         assertEquals("NOT_NULL", violations[0].getMessage());
         assertEquals("NOT_NULL", violations[1].getMessage());
      }

      e.name = "asdads";
      e.value = 5;
      v.assertValid(e);
   }

   public void testValidatorAssertField() throws Exception {
      final Field f = TestEntity.class.getField("name");

      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      try {
         v.assertValidFieldValue(e, f, null);
         fail();
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertEquals(1, violations.length);
         assertEquals("NOT_NULL", violations[0].getMessage());
      }

      v.assertValidFieldValue(e, f, "test");
   }
}
