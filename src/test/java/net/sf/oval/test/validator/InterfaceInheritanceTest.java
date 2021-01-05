/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.IsInvariant;
import net.sf.oval.configuration.annotation.Validatable;
import net.sf.oval.constraint.NotNull;

/**
 * @author Sebastian Thomschke
 */
public class InterfaceInheritanceTest {

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

      @Override
      public String getName() {
         return name;
      }
   }

   @Test
   public void testInheritance() {
      final Validator validator = new Validator();

      final DefaultEntity e = new DefaultEntity();

      final List<ConstraintViolation> violations = validator.validate(e);
      assertThat(violations).hasSize(1);
      assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");
   }
}
