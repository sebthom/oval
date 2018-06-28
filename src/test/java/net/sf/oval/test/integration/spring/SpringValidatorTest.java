/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.integration.spring;

import org.springframework.validation.BindException;

import junit.framework.TestCase;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNegative;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.integration.spring.SpringValidator;

/**
 * @author Sebastian Thomschke
 */
public class SpringValidatorTest extends TestCase {
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

   public void testSpringValidator() {
      final SpringValidator v = new SpringValidator(new Validator());
      final Entity e = new Entity();
      {
         e.name = null;
         e.age = -1;
         final BindException errors = new BindException(e, e.getClass().getName());
         v.validate(e, errors);
         assertEquals(2, errors.getErrorCount());
         assertEquals(2, errors.getFieldErrorCount());
         assertEquals(null, errors.getFieldError("name").getRejectedValue());
         assertTrue(errors.getFieldError("name").getCodes()[0].startsWith("E1"));
         assertEquals(-1, errors.getFieldError("age").getRejectedValue());
         assertTrue(errors.getFieldError("age").getCodes()[0].startsWith("E2"));
      }
      {
         final BindException errors = new BindException(e, e.getClass().getName());
         e.name = "";
         e.age = -1;
         v.validate(e, errors);
         assertEquals(1, errors.getErrorCount());
         assertEquals(1, errors.getFieldErrorCount());
         assertEquals(-1, errors.getFieldError("age").getRejectedValue());
         assertTrue(errors.getFieldError("age").getCodes()[0].startsWith("E2"));
      }
      {
         final BindException errors = new BindException(e, e.getClass().getName());
         e.name = null;
         e.age = 0;
         v.validate(e, errors);
         assertEquals(1, errors.getErrorCount());
         assertEquals(1, errors.getFieldErrorCount());
         assertEquals(null, errors.getFieldError("name").getRejectedValue());
         assertTrue(errors.getFieldError("name").getCodes()[0].startsWith("E1"));
      }
      {
         final BindException errors = new BindException(e, e.getClass().getName());
         e.name = "";
         e.age = 0;
         v.validate(e, errors);
         assertEquals(0, errors.getErrorCount());
         assertEquals(0, errors.getFieldErrorCount());
      }
   }
}
