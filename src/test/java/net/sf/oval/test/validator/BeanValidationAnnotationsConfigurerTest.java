/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.validator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer;

/**
 * @author Sebastian Thomschke
 *
 */
public class BeanValidationAnnotationsConfigurerTest extends TestCase {
   @Entity
   protected static class TestEntity {
      @NotNull(message = "NOT_NULL")
      @Size.List(@Size(max = 4))
      public String code;

      public String description;

      @NotNull(message = "NOT_NULL")
      @Valid
      public TestEntity parent;

      @Valid
      public TestEntity sibling;

      @Valid
      public Collection<@NotNull(message = "ELEMENT_NOT_NULL") TestEntity> children;

      @NotNull(message = "NOT_NULL")
      public String getDescription() {
         return description;
      }
   }

   public void testBeanValidationAnnotationsConfigurer() {
      final Validator v = new Validator(new BeanValidationAnnotationsConfigurer());
      List<ConstraintViolation> violations;

      final TestEntity entity = new TestEntity();

      {
         violations = v.validate(entity);
         // code is null
         // description is null
         // ref1 is null
         assertEquals(3, violations.size());
         assertNull(violations.get(0).getInvalidValue());
         assertNull(violations.get(1).getInvalidValue());
         assertNull(violations.get(2).getInvalidValue());
         assertEquals("NOT_NULL", violations.get(0).getMessage());
         assertEquals("NOT_NULL", violations.get(1).getMessage());
         assertEquals("NOT_NULL", violations.get(2).getMessage());
      }

      {
         entity.code = "";
         entity.description = "";
         entity.parent = new TestEntity();

         violations = v.validate(entity);
         // ref1 is invalid
         assertEquals(1, violations.size());
      }

      {
         entity.parent.code = "";
         entity.parent.description = "";
         entity.parent.parent = entity;

         violations = v.validate(entity);
         assertEquals(0, violations.size());
      }

      {
         entity.sibling = new TestEntity();

         violations = v.validate(entity);
         // sibling is invalid
         assertEquals(1, violations.size());
         assertEquals(true, violations.get(0).getMessage().contains("sibling"));
      }

      {
         entity.sibling.code = "";
         entity.sibling.description = "";
         entity.sibling.parent = entity;

         violations = v.validate(entity);
         assertEquals(0, violations.size());
      }

      // Size test
      {
         entity.code = "12345";
         violations = v.validate(entity);
         // code is too long
         assertEquals(1, violations.size());
         entity.code = "";
      }

      // Valid test
      {
         entity.children = new ArrayList<TestEntity>();
         final TestEntity d = new TestEntity();
         entity.children.add(d);

         violations = v.validate(entity);
         assertEquals(1, violations.size());

         d.code = "";
         d.description = "";
         d.parent = entity;

         violations = v.validate(entity);
         assertEquals(0, violations.size());
      }
   }
}
