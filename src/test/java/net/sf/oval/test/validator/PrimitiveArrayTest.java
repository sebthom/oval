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
import net.sf.oval.constraint.Max;
import net.sf.oval.constraint.MaxSize;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class PrimitiveArrayTest {
   public static class Account {
      @MinSize(value = 1, message = "MIN_SIZE")
      @MaxSize(value = 4, message = "MAX_SIZE")
      @Max(value = 10, message = "MAX")
      @NotNull(message = "NOT_NULL")
      public int[] items = new int[] {};

   }

   @Test
   public void testPrimitiveArray() {
      final Validator validator = new Validator();
      final Account account = new Account();

      // test min size
      List<ConstraintViolation> violations = validator.validate(account);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("MIN_SIZE");

      // test valid
      account.items = new int[] {1};
      violations = validator.validate(account);
      assertThat(violations).isEmpty();

      // test max size
      account.items = new int[] {1, 2, 3, 4, 5};
      violations = validator.validate(account);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("MAX_SIZE");

      // test attribute not null
      account.items = null;
      violations = validator.validate(account);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");

      // test elements max
      account.items = new int[] {1, 100};
      violations = validator.validate(account);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("MAX");
   }
}
