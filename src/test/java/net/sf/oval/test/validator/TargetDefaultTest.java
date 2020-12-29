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

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.MinLength;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.ValidationFailedException;

/**
 * @author Sebastian Thomschke
 */
public class TargetDefaultTest {

   static class Level1 {
      @MinSize(value = 4, target = "level3.array", message = "LEVEL3_ARRAY_TOO_SMALL")
      @MinLength(value = 4, appliesTo = ConstraintTarget.VALUES, target = "level3.array", message = "LEVEL3_ARRAY_ITEM_TOO_SMALL")
      @NotNull(target = "level3.name", message = "LEVEL3_NAME_IS_NULL")
      Level2 level2a;

      // illegal path, results in an InvalidConfigurationException
      @NotNull(target = "level3.foobar")
      Level2 level2b;
   }

   static class Level2 {

      @NotNull(target = "list")
      Level3 level3;
   }

   static class Level3 {
      String name;

      String[] array;

      List<List<String>> list;
   }

   @Test
   public void testTarget() {
      final Validator v = new Validator();
      List<ConstraintViolation> violations;

      final Level1 lv1 = new Level1();
      assertThat(v.validate(lv1)).isEmpty();

      lv1.level2a = new Level2();
      lv1.level2b = new Level2();
      assertThat(v.validate(lv1)).isEmpty();

      lv1.level2a.level3 = new Level3();
      violations = v.validate(lv1);
      assertThat(violations).hasSize(1);
      lv1.level2a.level3.name = "foo";

      lv1.level2a.level3.array = new String[] {};
      violations = v.validate(lv1);
      assertThat(violations).hasSize(1);
      ConstraintViolation violation = violations.get(0);
      assertThat(violation.getContext()).isInstanceOf(FieldContext.class);
      assertThat(violation.getMessage()).isEqualTo("LEVEL3_ARRAY_TOO_SMALL");

      lv1.level2a.level3.array = new String[] {"123", "1234", "1234", "1234"};
      violations = v.validate(lv1);
      assertThat(violations).hasSize(1);
      violation = violations.get(0);
      assertThat(violation.getContext()).isInstanceOf(FieldContext.class);
      assertThat(violation.getMessage()).isEqualTo("LEVEL3_ARRAY_ITEM_TOO_SMALL");

      try {
         lv1.level2b.level3 = new Level3();
         v.validate(lv1);
         failBecauseExceptionWasNotThrown(ValidationFailedException.class);
      } catch (final ValidationFailedException ex) {
         assertThat(ex.getCause()).isInstanceOf(InvalidConfigurationException.class);
      }
   }
}
