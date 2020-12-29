/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class InnerClassTest {

   @Guarded
   protected static class TestEntity {
      @Guarded(applyFieldConstraintsToSetters = true)
      protected static class InnerClassGuarded {
         @NotNull
         protected String name;

         protected InnerClassGuarded(final String name) {
            this.name = name;
         }

         public void setName(final String name) {
            this.name = name;
         }
      }

      protected static class InnerClassNotGuarded {
         @NotNull
         protected String name;

         /**
          * the @PostValidateObject annotation should lead to a warning by the ApiUsageAuditor
          */
         protected InnerClassNotGuarded(final String name) {
            this.name = name;
         }

         public void setName(final String name) {
            this.name = name;
         }
      }
   }

   @Test
   @SuppressWarnings("unused")
   public void testInnerClassGuarded() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);
      guard.setInvariantsEnabled(true);

      try {
         new TestEntity.InnerClassGuarded(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }

      final TestEntity.InnerClassGuarded instance = new TestEntity.InnerClassGuarded("");

      try {
         instance.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   /**
    * test that specified constraints for inner classes not marked with @Constrained
    * are ignored
    */
   @Test
   public void testInnerClassNotGuarded() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestEntity.InnerClassNotGuarded instance = new TestEntity.InnerClassNotGuarded(null);
      instance.setName(null);
   }
}
