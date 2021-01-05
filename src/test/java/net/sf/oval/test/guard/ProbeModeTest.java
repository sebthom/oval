/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.Length;
import net.sf.oval.constraint.MatchPattern;
import net.sf.oval.constraint.NotEmpty;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.ProbeModeListener;

/**
 * @author Sebastian Thomschke
 */
public class ProbeModeTest {

   @Guarded
   protected static class Person {
      @NotNull(message = "NOT_NULL")
      private String firstName = "";

      @NotNull(message = "NOT_NULL")
      private String lastName = "";

      @NotNull(message = "NOT_NULL")
      @Length(max = 6, message = "LENGTH")
      @NotEmpty(message = "NOT_EMPTY")
      @MatchPattern(pattern = "^[0-9]*$", message = "REG_EX")
      private String zipCode = "1";

      public String getFirstName() {
         return firstName;
      }

      public String getLastName() {
         return lastName;
      }

      public String getZipCode() {
         return zipCode;
      }

      public void setFirstName(@AssertFieldConstraints final String firstName) {
         this.firstName = firstName;
      }

      public void setLastName(@AssertFieldConstraints final String lastName) {
         this.lastName = lastName;
      }

      public void setZipCode(@AssertFieldConstraints final String zipCode) {
         this.zipCode = zipCode;
      }
   }

   @Test
   public void testProbeModeWithIllegalValues() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Person p = new Person();
      guard.enableProbeMode(p);

      p.setFirstName(null);
      p.setLastName(null);
      p.setZipCode("abcde");
      final ProbeModeListener result = guard.disableProbeMode(p);

      assertThat(p.getFirstName()).isEmpty();
      assertThat(p.getLastName()).isEmpty();
      assertThat(p.getZipCode()).isEqualTo("1");
      assertThat(result.getConstraintsViolatedExceptions()).hasSize(3);
      assertThat(result.getConstraintViolations()).hasSize(3);
      try {
         result.commit();
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   @Test
   public void testProbeModeWithValidValues() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Person p = new Person();
      guard.enableProbeMode(p);

      p.setFirstName("John");
      p.setLastName("Doe");
      p.setZipCode("12345");

      final ProbeModeListener result = guard.disableProbeMode(p);

      assertThat(p.getFirstName()).isEmpty();
      assertThat(p.getLastName()).isEmpty();
      assertThat(p.getZipCode()).isEqualTo("1");
      assertThat(result.getConstraintsViolatedExceptions()).isEmpty();
      assertThat(result.getConstraintViolations()).isEmpty();

      result.commit();

      assertThat(p.getFirstName()).isEqualTo("John");
      assertThat(p.getLastName()).isEqualTo("Doe");
      assertThat(p.getZipCode()).isEqualTo("12345");
   }
}
