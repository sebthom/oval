/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.integration.spring;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.validation.BindException;

import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNegative;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.integration.spring.SpringValidator;

/**
 * @author Sebastian Thomschke
 */
public class SpringValidatorTest {

   public static class Entity {
      @NotNull(errorCode = "E1", message = "M1")
      protected String name;

      @NotNegative(errorCode = "E2", message = "M2")
      protected int age;

      public int getAge() {
         return age;
      }

      public String getName() {
         return name;
      }
   }

   @Test
   public void testSpringValidator() {
      final SpringValidator v = new SpringValidator(new Validator());
      final Entity e = new Entity();
      {
         e.name = null;
         e.age = -1;
         final BindException errors = new BindException(e, e.getClass().getName());
         v.validate(e, errors);
         assertThat(errors.getErrorCount()).isEqualTo(2);
         assertThat(errors.getFieldErrorCount()).isEqualTo(2);
         assertThat(errors.getFieldError("name").getRejectedValue()).isNull();
         assertThat(errors.getFieldError("name").getCodes()[0]).startsWith("E1");
         assertThat(errors.getFieldError("age").getRejectedValue()).isEqualTo(-1);
         assertThat(errors.getFieldError("age").getCodes()[0]).startsWith("E2");
      }
      {
         final BindException errors = new BindException(e, e.getClass().getName());
         e.name = "";
         e.age = -1;
         v.validate(e, errors);
         assertThat(errors.getErrorCount()).isEqualTo(1);
         assertThat(errors.getFieldErrorCount()).isEqualTo(1);
         assertThat(errors.getFieldError("age").getRejectedValue()).isEqualTo(-1);
         assertThat(errors.getFieldError("age").getCodes()[0]).startsWith("E2");
      }
      {
         final BindException errors = new BindException(e, e.getClass().getName());
         e.name = null;
         e.age = 0;
         v.validate(e, errors);
         assertThat(errors.getErrorCount()).isEqualTo(1);
         assertThat(errors.getFieldErrorCount()).isEqualTo(1);
         assertThat(errors.getFieldError("name").getRejectedValue()).isNull();
         assertThat(errors.getFieldError("name").getCodes()[0]).startsWith("E1");
      }
      {
         final BindException errors = new BindException(e, e.getClass().getName());
         e.name = "";
         e.age = 0;
         v.validate(e, errors);
         assertThat(errors.getErrorCount()).isZero();
         assertThat(errors.getFieldErrorCount()).isZero();
      }
   }
}
