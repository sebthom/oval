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

import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.configuration.annotation.Validatable;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class InterfaceInheritanceTest extends TestCase {

   public interface EntityA {

      @IsInvariant
      @NotNull(message = "NOT_NULL")
      String getName();
   }

   public interface EntityB {

      @IsInvariant
      @NotNull(message = "NOT_EMPTY")
      String getName();
   }

   @Validatable(excludedInterfaces = EntityB.class)
   public static class DefaultEntity implements EntityA, EntityB {

      private String name;

      /**
       * @return the name
       */
      @Override
      public String getName() {
         return name;
      }
   }

   public void testInheritance() {
      final Validator validator = new Validator();

      final DefaultEntity e = new DefaultEntity();

      final List<ConstraintViolation> violations = validator.validate(e);
      assertEquals(1, violations.size());
      assertEquals(violations.get(0).getMessage(), "NOT_NULL");
   }
}
