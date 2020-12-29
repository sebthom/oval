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
