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
import net.sf.oval.constraint.Assert;

/**
 * @author Sebastian Thomschke
 */
public class AssertOGNLTest {

   @Assert(expr = "_this.firstName!=null && _this.lastName!=null && (_this.firstName.length() + _this.lastName.length() > 9)", lang = "ognl", message = "C0")
   public static class Person {
      @Assert(expr = "_value!=null", lang = "ognl", message = "C1")
      public String firstName;

      @Assert(expr = "_value!=null", lang = "ognl", message = "C2")
      public String lastName;

      @Assert(expr = "_value!=null && _value.length()>0 && _value.length()<7", lang = "ognl", message = "C3")
      public String zipCode;
   }

   @Test
   public void testOGNLExpression() {
      final Validator validator = new Validator();

      // test not null
      final Person p = new Person();
      List<ConstraintViolation> violations = validator.validate(p);
      assertThat(violations).hasSize(4);

      // test max length
      p.firstName = "Mike";
      p.lastName = "Mahoney";
      p.zipCode = "1234567"; // too long
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("C3");

      // test not empty
      p.zipCode = "";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("C3");

      // test ok
      p.zipCode = "wqeew";
      violations = validator.validate(p);
      assertThat(violations).isEmpty();

      // test object-level constraint
      p.firstName = "12345";
      p.lastName = "1234";
      violations = validator.validate(p);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("C0");
   }
}
