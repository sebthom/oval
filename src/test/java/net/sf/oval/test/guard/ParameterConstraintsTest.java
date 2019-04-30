/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import java.util.List;

import junit.framework.TestCase;
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
public class ParameterConstraintsTest extends TestCase {
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
         fail();
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertNotNull(violations);
         assertEquals(1, violations.length);
         assertEquals("NOT_NULL", violations[0].getMessage());
         assertTrue(violations[0].getContext() instanceof ConstructorParameterContext);
      }

      new TestEntity("test");

      /*
       * Testing Constructor 2
       * invariant constraint
       */
      try {
         new TestEntity(null, 100);
         fail();
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertNotNull(violations);
         assertEquals(1, violations.length);
         assertEquals("NOT_NULL", violations[0].getMessage());
         assertTrue(violations[0].getContext() instanceof FieldContext);
      }
   }

   public void testMethodParameters() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      try {
         final TestEntity t1 = new TestEntity("");
         t1.setName(null);
         fail();
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertNotNull(violations);
         assertTrue(violations.length > 0);
         assertEquals("NOT_NULL", violations[0].getMessage());
      }

      try {
         final TestEntity t1 = new TestEntity("");
         t1.setName("12345678");
         fail();
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertNotNull(violations);
         assertTrue(violations.length > 0);
         assertEquals("LENGTH", violations[0].getMessage());
      }
   }

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
      assertEquals(2, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());
      assertEquals("LENGTH", violations.get(1).getMessage());

      guard.removeListener(va, entity);
   }
}
