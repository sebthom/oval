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
import net.sf.oval.context.FieldContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.PostValidateThis;

/**
 * @author Sebastian Thomschke
 */
public class CheckInvariantsTest extends TestCase {

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

   @SuppressWarnings("unused")
   public void testCheckInvariants() {

      try {
         new TestEntity("a", null);
         fail();
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertNotNull(violations);
         assertEquals(1, violations.length);
         assertEquals("NOT_NULL", violations[0].getMessage());
         assertTrue(violations[0].getContext() instanceof FieldContext);
      }

      new TestEntity(null, null);

      final TestEntity e = new TestEntity("a", "b");
      try {
         e.setFieldB(null);
         fail();
      } catch (final ConstraintsViolatedException ex) {
         final ConstraintViolation[] violations = ex.getConstraintViolations();
         assertNotNull(violations);
         assertEquals(1, violations.length);
         assertEquals("NOT_NULL", violations[0].getMessage());
         assertTrue(violations[0].getContext() instanceof FieldContext);
      }
   }

}
