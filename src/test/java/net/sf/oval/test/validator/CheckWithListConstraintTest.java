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
import net.sf.oval.constraint.CheckWith;
import net.sf.oval.constraint.CheckWithCheck.SimpleCheck;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class CheckWithListConstraintTest {

   protected static class TestEntity1 {
      protected static class NameCheck1 implements SimpleCheck {
         protected static final long serialVersionUID = 1L;

         @Override
         public boolean isSatisfied(final Object validatedObject, final Object value, final OValContext context, final Validator validator) {
            final String name = (String) value;

            if (name == null)
               return false;
            if (name.length() == 0)
               return false;
            if (name.length() > 4)
               return false;
            return true;
         }
      }

      @CheckWith.List(@CheckWith(value = NameCheck1.class, ignoreIfNull = false))
      public String name;
   }

   protected static class TestEntity2 {
      protected static class NameCheck2 implements SimpleCheck {
         protected static final long serialVersionUID = 1L;

         @Override
         public boolean isSatisfied(final Object validatedObject, final Object value, final OValContext context, final Validator validator) {
            return ((TestEntity2) validatedObject).isValidName((String) value);
         }
      }

      @CheckWith(NameCheck2.class)
      @NotNull
      public String name;

      protected boolean isValidName(final String name) {
         if (name.length() == 0)
            return false;
         if (name.length() > 4)
            return false;
         return true;
      }
   }

   @Test
   public void testCheckWith1() {
      final Validator validator = new Validator();

      final TestEntity1 t = new TestEntity1();

      List<ConstraintViolation> violations;

      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "";
      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "12345";
      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "1234";
      violations = validator.validate(t);
      assertThat(violations).isEmpty();
   }

   @Test
   public void testCheckWith2() {
      final Validator validator = new Validator();

      final TestEntity2 t = new TestEntity2();

      List<ConstraintViolation> violations;

      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "";
      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "12345";
      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "1234";
      violations = validator.validate(t);
      assertThat(violations).isEmpty();
   }
}
