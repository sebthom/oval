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
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.JPAAnnotationsConfigurer;

/**
 * @author Sebastian Thomschke
 *
 */
public class JPAAnnotationsConfigurerTest {

   @Entity
   protected static class TestEntity {
      // -> @NotNull
      @Basic(optional = false)
      // -> @MaxLength(4)
      @Column(length = 4)
      public String code;

      public String description;

      // -> @NotNull & @AssertValid
      @ManyToOne(optional = false)
      public TestEntity ref1;

      // -> @AssertValid
      @OneToOne(optional = true)
      public TestEntity ref2;

      // -> @AssertValid
      @OneToMany
      public Collection<TestEntity> refs;

      // -> @NotNull
      @Column(nullable = false)
      public String getDescription() {
         return description;
      }
   }

   @Test
   public void testJPAAnnotationsConfigurer() {
      final Validator v = new Validator(new JPAAnnotationsConfigurer());
      List<ConstraintViolation> violations;

      final TestEntity entity = new TestEntity();

      {
         violations = v.validate(entity);
         // code is null
         // description is null
         // ref1 is null
         assertThat(violations).hasSize(3);
         assertThat(violations.get(0).getInvalidValue()).isNull();
         assertThat(violations.get(1).getInvalidValue()).isNull();
         assertThat(violations.get(2).getInvalidValue()).isNull();
      }

      {
         entity.code = "";
         entity.description = "";
         entity.ref1 = new TestEntity();

         violations = v.validate(entity);
         // ref1 is invalid
         System.out.println(violations);
         assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsOnly( //
            TestEntity.class.getName() + ".ref1.ref1 cannot be null", //
            TestEntity.class.getName() + ".ref1.code cannot be null", //
            TestEntity.class.getName() + ".ref1.getDescription() cannot be null" //
         );
      }

      {
         entity.ref1.code = "";
         entity.ref1.description = "";
         entity.ref1.ref1 = entity;

         violations = v.validate(entity);
         assertThat(violations).isEmpty();
      }

      {
         entity.ref2 = new TestEntity();

         violations = v.validate(entity);
         // ref2 is invalid
         assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsOnly( //
            TestEntity.class.getName() + ".ref2.ref1 cannot be null", //
            TestEntity.class.getName() + ".ref2.code cannot be null", //
            TestEntity.class.getName() + ".ref2.getDescription() cannot be null" //
         );
      }

      {
         entity.ref2.code = "";
         entity.ref2.description = "";
         entity.ref2.ref1 = entity;

         violations = v.validate(entity);
         assertThat(violations).isEmpty();
      }

      // Column length test
      {
         entity.code = "12345";
         violations = v.validate(entity);
         // code is too long
         assertThat(violations).hasSize(1);

         entity.code = "";
      }

      // OneToMany test
      {
         entity.refs = new ArrayList<>();
         final TestEntity d = new TestEntity();
         entity.refs.add(d);

         violations = v.validate(entity);
         assertThat(violations.stream().map(ConstraintViolation::getMessage)).containsOnly( //
            TestEntity.class.getName() + ".refs[0].ref1 cannot be null", //
            TestEntity.class.getName() + ".refs[0].code cannot be null", //
            TestEntity.class.getName() + ".refs[0].getDescription() cannot be null" //
         );

         d.code = "";
         d.description = "";
         d.ref1 = entity;

         violations = v.validate(entity);
         assertThat(violations).isEmpty();
      }
   }
}
