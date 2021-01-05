/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class StaticFieldsAndGettersTest {

   protected static class TestEntity {
      @NotNull
      static String staticA;
      static String staticB;

      @IsInvariant
      @NotNull
      public static String getStaticB() {
         return staticB;
      }

      @NotNull
      protected String nonstaticA;
      protected String nonstaticB;

      @IsInvariant
      @NotNull
      public String getNonstaticB() {
         return nonstaticB;
      }
   }

   @Test
   public void testNonstaticValidation() {
      final Validator validator = new Validator();

      TestEntity.staticA = null;
      TestEntity.staticB = null;

      // test that only non static fields are validated
      final TestEntity t = new TestEntity();
      List<ConstraintViolation> violations = validator.validate(t);
      assertThat(violations).hasSize(2);

      t.nonstaticA = "";
      t.nonstaticB = "";

      violations = validator.validate(t);
      assertThat(violations).isEmpty();
   }

   @Test
   public void testStaticValidation() {
      final Validator validator = new Validator();

      TestEntity.staticA = null;
      TestEntity.staticB = null;

      // test that only static fields are validated
      List<ConstraintViolation> violations = validator.validate(TestEntity.class);
      assertThat(violations).hasSize(2);

      TestEntity.staticA = "";
      TestEntity.staticB = "";

      violations = validator.validate(TestEntity.class);
      assertThat(violations).isEmpty();
   }
}
