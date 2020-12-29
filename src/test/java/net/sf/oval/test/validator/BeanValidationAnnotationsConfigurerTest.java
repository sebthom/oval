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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer;

/**
 * @author Sebastian Thomschke
 */
public class BeanValidationAnnotationsConfigurerTest {

   @Entity
   protected static class TestEntity {
      private final List<String> description = new ArrayList<>();

      @NotNull(message = "CODE_NOT_NULL")
      @Size.List(@Size(max = 4))
      public String code;

      @NotNull(message = "PARENT_NOT_NULL")
      @Valid
      public TestEntity parent;

      @Valid
      public TestEntity sibling;

      @Valid
      public Collection<@NotNull(message = "CHILDREN_ELEMENT_NOT_NULL") TestEntity> children = new ArrayList<>();

      @Valid
      public Map<@NotNull(message = "KEY_NOT_NULL") String, @NotNull(message = "VALUE_NOT_NULL") String> props = new HashMap<>();

      @NotEmpty(message = "DESCRIPTION_NOT_EMPTY")
      public List<@NotNull(message = "DESCRIPTION_ELEMENT_NOT_NULL") String> getDescription() {
         return description;
      }
   }

   @Test
   public void testBeanValidationAnnotationsConfigurer() {
      final Validator v = new Validator(new BeanValidationAnnotationsConfigurer());
      List<ConstraintViolation> violations;

      final TestEntity entity = new TestEntity();

      {
         violations = v.validate(entity);
         // code is null
         // description is empty
         // parent is null
         assertThat(violations).hasSize(3);

         final String[] msgs = {violations.get(0).getMessage(), violations.get(1).getMessage(), violations.get(2).getMessage()};
         Arrays.sort(msgs);
         assertThat(msgs).isEqualTo(new String[] {"CODE_NOT_NULL", "DESCRIPTION_NOT_EMPTY", "PARENT_NOT_NULL"});
      }

      {
         entity.code = "";
         entity.description.add("");
         entity.parent = new TestEntity();

         violations = v.validate(entity);
         // parent is invalid
         assertThat(violations).hasSize(1);
      }

      {
         entity.parent.code = "";
         entity.parent.description.add("");
         entity.parent.parent = entity;

         violations = v.validate(entity);
         assertThat(violations).isEmpty();
      }

      {
         entity.sibling = new TestEntity();

         violations = v.validate(entity);
         // sibling is invalid
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).contains("sibling");
      }

      {
         entity.sibling.code = "";
         entity.sibling.description.add("");
         entity.sibling.parent = entity;

         violations = v.validate(entity);
         assertThat(violations).isEmpty();
      }

      // Size test
      {
         entity.code = "12345";
         violations = v.validate(entity);
         // code is too long
         assertThat(violations).hasSize(1);
         entity.code = "";
      }

      // Valid test
      {
         entity.children = new ArrayList<>();
         final TestEntity d = new TestEntity();
         entity.children.add(d);

         violations = v.validate(entity);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getErrorCode()).isEqualTo("net.sf.oval.constraint.AssertValid");

         d.code = "";
         d.description.add("");
         d.parent = entity;

         violations = v.validate(entity);
         assertThat(violations).isEmpty();
      }

      // No null in field collection test
      {
         entity.children = new ArrayList<>();

         violations = v.validate(entity);
         assertThat(violations).isEmpty();

         entity.children.add(null);

         violations = v.validate(entity);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).isEqualTo("CHILDREN_ELEMENT_NOT_NULL");
      }

      // No null in field map test
      {
         entity.children = new ArrayList<>();
         entity.props.put(null, "1");
         entity.props.put("1", null);

         violations = v.validate(entity);
         assertThat(violations).hasSize(2);
         final String[] msgs = {violations.get(0).getMessage(), violations.get(1).getMessage()};
         Arrays.sort(msgs);
         assertThat(msgs).isEqualTo(new String[] {"KEY_NOT_NULL", "VALUE_NOT_NULL"});

         entity.props.clear();
      }

      // No null in method return value collection test
      {
         entity.children = new ArrayList<>();

         entity.description.add(null);

         violations = v.validate(entity);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).isEqualTo("DESCRIPTION_ELEMENT_NOT_NULL");
      }

   }
}
