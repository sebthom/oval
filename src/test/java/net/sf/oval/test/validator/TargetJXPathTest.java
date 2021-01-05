/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.AssertTrue;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.MinLength;
import net.sf.oval.constraint.MinSize;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.context.FieldContext;
import net.sf.oval.exception.InvalidConfigurationException;
import net.sf.oval.exception.ValidationFailedException;

/**
 * @author Sebastian Thomschke
 */
public class TargetJXPathTest {

   public static class Level1 {
      @AssertTrue(target = "jxpath:.[@visible=0]/visible" /* find an item where visible='false' and select the 'visible' property for testing */)
      protected List<Thing> things = new ArrayList<>();

      @MinSize(target = "jxpath:level3/array", value = 4, message = "LEVEL3_ARRAY_TOO_SMALL")
      @MinLength(target = "jxpath:level3/array", value = 4, appliesTo = ConstraintTarget.VALUES, message = "LEVEL3_ARRAY_ITEM_TOO_SMALL")
      @MaxLength(target = "jxpath:level3/array[1]", value = 5, message = "LEVEL3_ARRAY_FIRST_ITEM_TOO_LONG")
      @NotNull(target = "jxpath:level3/name", message = "LEVEL3_NAME_IS_NULL")
      protected Level2 level2a;

      // illegal path, results in an InvalidConfigurationException
      @NotNull(target = "jxpath:level3/foobar")
      protected Level2 level2b;

      public Level2 getLevel2a() {
         return level2a;
      }

      public Level2 getLevel2b() {
         return level2b;
      }
   }

   public static class Level2 {
      protected Level3 level3;

      public Level3 getLevel3() {
         return level3;
      }
   }

   public static class Level3 {
      protected String name;
      protected String[] array;

      public String[] getArray() {
         return array;
      }

      public String getName() {
         return name;
      }
   }

   public static class Thing {
      private final boolean visible;

      public Thing(final boolean visible) {
         this.visible = visible;
      }

      public boolean isVisible() {
         return visible;
      }
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

      lv1.things.add(new Thing(true));
      assertThat(v.validate(lv1)).isEmpty();
      lv1.things.add(new Thing(false));
      assertThat(v.validate(lv1)).hasSize(1);
      lv1.things.clear();

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

      lv1.level2a.level3.array = new String[] {"123456789", "1234", "1234", "1234"};
      violations = v.validate(lv1);
      assertThat(violations).hasSize(1);
      violation = violations.get(0);
      assertThat(violation.getContext()).isInstanceOf(FieldContext.class);
      assertThat(violation.getMessage()).isEqualTo("LEVEL3_ARRAY_FIRST_ITEM_TOO_LONG");

      lv1.level2a.level3.array = new String[] {"123456789", "123456789", "123456789", "123456789"};
      violations = v.validate(lv1);
      assertThat(violations).hasSize(1);
      violation = violations.get(0);
      assertThat(violation.getContext()).isInstanceOf(FieldContext.class);
      assertThat(violation.getMessage()).isEqualTo("LEVEL3_ARRAY_FIRST_ITEM_TOO_LONG");

      try {
         lv1.level2b.level3 = new Level3();
         v.validate(lv1);
         failBecauseExceptionWasNotThrown(ValidationFailedException.class);
      } catch (final ValidationFailedException ex) {
         assertThat(ex.getCause()).isInstanceOf(InvalidConfigurationException.class);
      }
   }
}
