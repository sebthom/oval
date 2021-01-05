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
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ProfilesTest {

   protected static class Person {
      @NotNull(/* profiles = { "default" }, */message = "NOTNULL")
      public String city;

      @NotNull(profiles = {"profile1"}, message = "NOTNULL1")
      public String firstName;

      @NotNull(profiles = {"profile2", "profile3"}, message = "NOTNULL2")
      public String lastName;

      @NotNull(profiles = {"profile3", "profile4"}, message = "NOTNULL3")
      public String zipCode;
   }

   @Test
   public void testAdhocProfiles() {
      final Validator validator = new Validator();

      // disable all profiles = no constraints by default
      validator.disableAllProfiles();
      final Person p = new Person();
      List<ConstraintViolation> violations = validator.validate(p, (String[]) null);
      assertThat(violations).isEmpty();
      violations = validator.validate(p, "profile1");
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOTNULL1");
      violations = validator.validate(p, "profile1", "profile2");
      assertThat(violations).hasSize(2);

      // enable all profiles = all constraints by default
      validator.enableAllProfiles();
      violations = validator.validate(p, (String[]) null);
      assertThat(violations).hasSize(4);
      violations = validator.validate(p, "profile1");
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOTNULL1");
      violations = validator.validate(p, "profile1", "profile2");
      assertThat(violations).hasSize(2);
   }

   @Test
   public void testProfilesGloballyDisabled() {
      final Validator validator = new Validator();

      // disable all profiles = no constraints
      validator.disableAllProfiles();
      assertThat(validator.isProfileEnabled("profile1")).isFalse();
      assertThat(validator.isProfileEnabled("profile2")).isFalse();
      assertThat(validator.isProfileEnabled("profile3")).isFalse();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).isEmpty();
      }

      // enable profile 1
      validator.enableProfile("profile1");
      assertThat(validator.isProfileEnabled("profile1")).isTrue();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).isEqualTo("NOTNULL1");
      }

      // enable profile 1 + 2
      validator.enableProfile("profile2");
      assertThat(validator.isProfileEnabled("profile2")).isTrue();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(2);
      }

      // enable profile 1 + 2 + 3
      validator.enableProfile("profile3");
      assertThat(validator.isProfileEnabled("profile3")).isTrue();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(3);
      }

      // enable profile 1 + 2 + 3 + 4
      assertThat(validator.isProfileEnabled("profile4")).isFalse();
      validator.enableProfile("profile4");
      assertThat(validator.isProfileEnabled("profile4")).isTrue();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(3);
      }

      // enable profile 1 + 2 + 3 + 4 + default
      assertThat(validator.isProfileEnabled("default")).isFalse();
      validator.enableProfile("default");
      assertThat(validator.isProfileEnabled("default")).isTrue();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(4);
      }
   }

   @Test
   public void testProfilesGloballyEnabled() {
      final Validator validator = new Validator();

      validator.enableAllProfiles();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(4);
      }

      assertThat(validator.isProfileEnabled("profile1")).isTrue();
      validator.disableProfile("profile1");
      assertThat(validator.isProfileEnabled("profile1")).isFalse();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(3);
      }

      assertThat(validator.isProfileEnabled("profile2")).isTrue();
      validator.disableProfile("profile2");
      assertThat(validator.isProfileEnabled("profile2")).isFalse();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(3);
      }

      assertThat(validator.isProfileEnabled("profile3")).isTrue();
      validator.disableProfile("profile3");
      assertThat(validator.isProfileEnabled("profile3")).isFalse();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(2);
         if ("NOTNULL".equals(violations.get(0).getMessage())) {
            assertThat(violations.get(1).getMessage()).isEqualTo("NOTNULL3");
         } else {
            assertThat(violations.get(0).getMessage()).isEqualTo("NOTNULL3");
            assertThat(violations.get(1).getMessage()).isEqualTo("NOTNULL");
         }
      }

      assertThat(validator.isProfileEnabled("profile4")).isTrue();
      validator.disableProfile("profile4");
      assertThat(validator.isProfileEnabled("profile4")).isFalse();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).hasSize(1);
      }

      assertThat(validator.isProfileEnabled("default")).isTrue();
      validator.disableProfile("default");
      assertThat(validator.isProfileEnabled("default")).isFalse();
      {
         final Person p = new Person();
         final List<ConstraintViolation> violations = validator.validate(p);
         assertThat(violations).isEmpty();
      }
   }
}
