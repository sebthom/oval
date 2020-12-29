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
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class ClassInheritanceTest {

   public abstract static class AbstractEntity {
      @NotNull(message = "NOT_NULL")
      private String name;

      public String getName() {
         return name;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   public static class EntityImpl extends AbstractEntity {
      // do nothing
   }

   @Test
   public void testInheritance() {
      final Validator validator = new Validator();

      final AbstractEntity e = new EntityImpl();

      final List<ConstraintViolation> violations = validator.validate(e);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");
   }
}
