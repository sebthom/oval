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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.JPAAnnotationsConfigurer;

/**
 * @author Sebastian Thomschke
 *
 */
public class JPAAnnotationsConfigurerTest extends TestCase {
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

   public void testJPAAnnotationsConfigurer() {
      final Validator v = new Validator(new JPAAnnotationsConfigurer());
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
      }

      {
         entity.code = "";
         entity.description = "";
         entity.ref1 = new TestEntity();

         violations = v.validate(entity);
         // ref1 is invalid
         assertEquals(1, violations.size());
      }

      {
         entity.ref1.code = "";
         entity.ref1.description = "";
         entity.ref1.ref1 = entity;

         violations = v.validate(entity);
         assertEquals(0, violations.size());
      }

      {
         entity.ref2 = new TestEntity();

         violations = v.validate(entity);
         // ref2 is invalid
         assertEquals(1, violations.size());
      }

      {
         entity.ref2.code = "";
         entity.ref2.description = "";
         entity.ref2.ref1 = entity;

         violations = v.validate(entity);
         assertEquals(0, violations.size());
      }

      // Column length test
      {
         entity.code = "12345";
         violations = v.validate(entity);
         // code is too long
         assertEquals(1, violations.size());

         entity.code = "";
      }

      // OneToMany test
      {
         entity.refs = new ArrayList<>();
         final TestEntity d = new TestEntity();
         entity.refs.add(d);

         violations = v.validate(entity);
         assertEquals(1, violations.size());

         d.code = "";
         d.description = "";
         d.ref1 = entity;

         violations = v.validate(entity);
         assertEquals(0, violations.size());
      }
   }
}
