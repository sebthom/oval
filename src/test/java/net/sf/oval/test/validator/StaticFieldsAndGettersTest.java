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

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class StaticFieldsAndGettersTest extends TestCase {
   protected static class TestEntity {
      @NotNull
      static String staticA;

      static String staticB;

      /**
       * @return the staticB
       */
      @IsInvariant
      @NotNull
      public static String getStaticB() {
         return staticB;
      }

      @NotNull
      protected String nonstaticA;

      protected String nonstaticB;

      /**
       * @return the nonstaticB
       */
      @IsInvariant
      @NotNull
      public String getNonstaticB() {
         return nonstaticB;
      }
   }

   public void testNonstaticValidation() {
      final Validator validator = new Validator();

      TestEntity.staticA = null;
      TestEntity.staticB = null;

      // test that only non static fields are validated
      final TestEntity t = new TestEntity();
      List<ConstraintViolation> violations = validator.validate(t);
      assertTrue(violations.size() == 2);

      t.nonstaticA = "";
      t.nonstaticB = "";

      violations = validator.validate(t);
      assertTrue(violations.size() == 0);
   }

   public void testStaticValidation() {
      final Validator validator = new Validator();

      TestEntity.staticA = null;
      TestEntity.staticB = null;

      // test that only static fields are validated
      List<ConstraintViolation> violations = validator.validate(TestEntity.class);
      assertTrue(violations.size() == 2);

      TestEntity.staticA = "";
      TestEntity.staticB = "";

      violations = validator.validate(TestEntity.class);
      assertTrue(violations.size() == 0);
   }
}
