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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
 */
public class AddingChecksTest extends TestCase {
   protected static class TestEntity {
      protected String name;

      protected TestEntity(final String name) {
         this.name = name;
      }

      /**
       * @param name the name to set
       */
      public void setName(final String name) {
         this.name = name;
      }
   }

   /**
    * programmatically add a NotNull constraint to the name field
    */
   public void testAddConstraintToField() throws Exception {
      final Validator validator = new Validator();

      final TestEntity entity = new TestEntity(null);
      assertEquals(validator.validate(entity).size(), 0);

      final Field field = TestEntity.class.getDeclaredField("name");
      final NotNullCheck notNullCheck = new NotNullCheck();
      notNullCheck.setMessage("NOT_NULL");

      validator.addChecks(field, notNullCheck);

      final List<ConstraintViolation> violations = validator.validate(entity);
      assertEquals(1, violations.size());
      assertEquals("NOT_NULL", violations.get(0).getMessage());
   }

   /**
    * try to programmatically add a NotNull constraint to the void setter
    * this should fail since the method is not a getter
    */
   public void testAddConstraintToGetter() throws Exception {
      final Validator validator = new Validator();

      try {
         final Method setter = TestEntity.class.getDeclaredMethod("setName", String.class);

         validator.addChecks(setter, new NotNullCheck());
         fail();
      } catch (final InvalidConfigurationException e) {
         // expected
      }
   }
}
