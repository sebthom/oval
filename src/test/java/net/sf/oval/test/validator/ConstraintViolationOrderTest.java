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
import net.sf.oval.constraint.EqualToField;
import net.sf.oval.constraint.HasSubstring;
import net.sf.oval.constraint.MaxLength;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintViolationOrderTest {

   protected static class TestEntity {
      @MaxLength(value = 5, message = "VIO1")
      @EqualToField(value = "value", message = "VIO2")
      @HasSubstring(value = "foo", message = "VIO3")
      public String name = "12345678";

      public String value = "123";
   }

   @Test
   public void testConstraintViolationOrder() {
      final TestEntity e = new TestEntity();
      final Validator v = new Validator();
      final List<ConstraintViolation> violations = v.validate(e);
      assertThat(violations).hasSize(3);
      assertThat(violations.get(0).getMessage()).isEqualTo("VIO1");
      assertThat(violations.get(1).getMessage()).isEqualTo("VIO2");
      assertThat(violations.get(2).getMessage()).isEqualTo("VIO3");
   }
}
