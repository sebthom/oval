/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import net.sf.oval.AbstractCheck;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.ValidationCycle;
import net.sf.oval.Validator;
import net.sf.oval.context.FieldContext;

/**
 * @author Sebastian Thomschke
 */
public class CustomConstraintViolationsTest {
   public static class CustomCheck extends AbstractCheck {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
         final Entity entity = (Entity) validatedObject;
         if (entity.message == null) {
            cycle.addConstraintViolation(new ConstraintViolation(this, "message cannot be null", validatedObject, null, Arrays.asList(new FieldContext(
               Entity.class, "message"))));
         }

         if (entity.name == null) {
            cycle.addConstraintViolation(new ConstraintViolation(this, "name cannot be null", validatedObject, null, Arrays.asList(new FieldContext(
               Entity.class, "name"))));
         }

         return true;
      }
   }

   public final class Entity {
      String name;
      String message;
   }

   @Test
   public void testMessages() {
      final Validator val = new Validator();
      val.addChecks(Entity.class, new CustomCheck());
      final List<ConstraintViolation> violations = val.validate(new Entity());
      assertThat(violations).hasSize(2);
   }
}
