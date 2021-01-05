/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import net.sf.oval.Check;
import net.sf.oval.ConstraintSet;
import net.sf.oval.constraint.AssertConstraintSet;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.NotEmptyCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.guard.ConstraintsViolatedAdapter;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetTest {

   @Guarded
   protected static class Person {
      private String zipCode;

      public String getZipCode() {
         return zipCode;
      }

      public void setZipCode(@AssertConstraintSet(id = "zipCode") final String zipCode) {
         this.zipCode = zipCode;
      }
   }

   @Test
   public void testConstraintSetValidation() {
      final ConstraintSet constraintSet = new ConstraintSet("zipCode");
      final List<Check> checks = new ArrayList<>();
      constraintSet.setChecks(checks);

      final NotNullCheck notNull = new NotNullCheck();
      notNull.setMessage("NOT_NULL");
      checks.add(notNull);

      final LengthCheck length = new LengthCheck();
      length.setMessage("LENGTH");
      length.setMax(6);
      checks.add(length);

      final NotEmptyCheck notEmpty = new NotEmptyCheck();
      notEmpty.setMessage("NOT_EMPTY");
      checks.add(notEmpty);

      final MatchPatternCheck matchPattern = new MatchPatternCheck();
      matchPattern.setMessage("MATCH_PATTERN");
      matchPattern.setPattern("^[0-9]*$", 0);
      checks.add(matchPattern);

      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      guard.addConstraintSet(constraintSet, false);

      {
         final Person p = new Person();

         TestGuardAspect.aspectOf().getGuard().enableProbeMode(p);

         final ConstraintsViolatedAdapter va = new ConstraintsViolatedAdapter();
         TestGuardAspect.aspectOf().getGuard().addListener(va, p);

         // test @Length(max=)
         p.setZipCode("1234567");
         assertThat(va.getConstraintsViolatedExceptions()).hasSize(1);
         assertThat(va.getConstraintViolations()).hasSize(1);
         assertThat(va.getConstraintViolations().get(0).getMessage()).isEqualTo("LENGTH");
         va.clear();

         // test @NotEmpty
         p.setZipCode("");
         assertThat(va.getConstraintsViolatedExceptions()).hasSize(1);
         assertThat(va.getConstraintViolations()).hasSize(1);
         assertThat(va.getConstraintViolations().get(0).getMessage()).isEqualTo("NOT_EMPTY");
         va.clear();

         // test @MatchPattern
         p.setZipCode("dffd34");
         assertThat(va.getConstraintsViolatedExceptions()).hasSize(1);
         assertThat(va.getConstraintViolations()).hasSize(1);
         assertThat(va.getConstraintViolations().get(0).getMessage()).isEqualTo("MATCH_PATTERN");
         va.clear();
      }

   }
}
