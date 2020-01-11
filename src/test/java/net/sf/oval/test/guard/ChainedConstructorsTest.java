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

import junit.framework.TestCase;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
@SuppressWarnings("unused")
public class ChainedConstructorsTest extends TestCase {
   @Guarded
   public static final class Entity {
      public Entity(@NotNull final Object param) {
         this(param.toString(), "whatever");
      }

      public Entity(@NotNull final String param1, @NotNull final String params2) {
         // do stuff
      }
   }

   public void testConstructorChaining() {
      try {
         new Entity(null);
         fail();
      } catch (final Exception ex) {
         // TODO: currently fails with an NPE instead of a ConstraintViolationException https://sourceforge.net/p/oval/bugs/83/
      }
   }
}
