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
import net.sf.oval.AbstractCheck;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class CustomConstraintViolationsTest extends TestCase {
   public static class CustomCheck extends AbstractCheck {
      private static final long serialVersionUID = 1L;

      @Override
      public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
         final Entity entity = (Entity) validatedObject;
         if (entity.message == null) {
            validator.reportConstraintViolation(new ConstraintViolation(this, "message cannot be null", validatedObject, null, new FieldContext(Entity.class,
               "message")));
         }

         if (entity.name == null) {
            validator.reportConstraintViolation(new ConstraintViolation(this, "name cannot be null", validatedObject, null, new FieldContext(Entity.class,
               "name")));
         }

         return true;
      }
   }

   public final class Entity {
      String name;
      String message;
   }

   public void testMessages() {
      final Validator val = new Validator();
      val.addChecks(Entity.class, new CustomCheck());
      final List<ConstraintViolation> violations = val.validate(new Entity());
      assertEquals(2, violations.size());
   }
}
