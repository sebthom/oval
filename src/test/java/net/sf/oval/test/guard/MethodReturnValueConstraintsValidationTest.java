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
import net.sf.oval.constraint.Assert;
import net.sf.oval.constraint.Length;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class MethodReturnValueConstraintsValidationTest extends TestCase {
   @Guarded
   public static class TestEntity {
      protected String name = "";

      /* we explicitly use _this.name here to check for circular issues, since OGNL and other
       * scripting languages (Groovy, MVEL, ...) will invoke the getter themselves to retrieve the property value
       * and will not directly access the field
       */
      @Assert(expr = "_this.name != null", lang = "bsh", message = "NOT_NULL")
      @Length(max = 4, message = "LENGTH")
      public String getName() {
         return name;
      }
   }

   public void testMethodReturnValueConstraintValidation() {
      final Guard guard = new Guard();

      TestGuardAspect.aspectOf().setGuard(guard);

      final TestEntity t = new TestEntity();

      try {
         t.name = null;
         t.getName();
         fail();
      } catch (final ConstraintsViolatedException e) {
         assertTrue(e.getConstraintViolations().length == 1);
         assertTrue(e.getConstraintViolations()[0].getMessage().equals("NOT_NULL"));
      }

      t.name = "testtest";

      try {
         t.getName();
         fail();
      } catch (final ConstraintsViolatedException e) {
         assertTrue(e.getConstraintViolations().length == 1);
         assertTrue(e.getConstraintViolations()[0].getMessage().equals("LENGTH"));
      }
   }
}
