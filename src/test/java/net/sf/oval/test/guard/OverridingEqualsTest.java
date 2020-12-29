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

import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class OverridingEqualsTest {

   @Guarded
   public static class Entity {
      protected int foo;

      @Override
      public boolean equals(final Object o) {
         final boolean retVal;
         if (o == null) {
            retVal = false;
         } else if (o instanceof Entity) {
            retVal = ((Entity) o).foo == foo;
         } else {
            retVal = false;
         }
         return retVal;
      }

      @Override
      public int hashCode() {
         return super.hashCode();
      }
   }

   @Test
   public void testGuarding() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Entity a1 = new Entity();
      a1.foo = 2;
      final Entity a2 = new Entity();
      a2.foo = 2;

      assertThat(a2).isEqualTo(a1);
   }
}
