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

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.constraint.ValidateWithMethod;

/**
 * @author Sebastian Thomschke
 */
public class ValidateWithMethodConstraintTest {

   protected static class BaseEntity {
      protected boolean isNameValid(final String name) {
         if (name == null)
            return false;
         if (name.length() == 0)
            return false;
         if (name.length() > 4)
            return false;
         return true;
      }
   }

   protected static class TestEntity extends BaseEntity {
      @ValidateWithMethod(methodName = "isNameValid", parameterType = String.class, ignoreIfNull = false)
      public String name;
   }

   @Test
   public void testCheckByMethod() {
      final Validator validator = new Validator();

      final TestEntity t = new TestEntity();

      List<ConstraintViolation> violations;

      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "";
      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "12345";
      violations = validator.validate(t);
      assertThat(violations).hasSize(1);

      t.name = "1234";
      violations = validator.validate(t);
      assertThat(violations).isEmpty();
   }
}
