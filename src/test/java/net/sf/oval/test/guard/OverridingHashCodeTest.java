/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
public class OverridingHashCodeTest {

   @Guarded
   public static class Entity {

      @Override
      public boolean equals(final Object obj) {
         return this == obj;
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }

      public void setFoo(@SuppressWarnings("unused") @NotNull final String s) {
         //
      }
   }

   @Test
   public void testGuarding() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);
      try {
         new Entity().setFoo(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         // expected
      }
   }
}
