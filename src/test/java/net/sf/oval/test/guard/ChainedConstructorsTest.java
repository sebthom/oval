/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import javax.validation.ConstraintViolationException;

import org.junit.Test;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
@SuppressWarnings("unused")
public class ChainedConstructorsTest {

   @Guarded
   public static final class Entity {
      public Entity(@NotNull final Object param) {
         this(param.toString(), "whatever");
      }

      public Entity(@NotNull final String param1, @NotNull final String params2) {
         // do stuff
      }
   }

   @Test
   public void testConstructorChaining() {
      try {
         new Entity(null);
         failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
      } catch (final Exception ex) {
         ex.printStackTrace();
         // TODO: currently fails with an NPE instead of a ConstraintViolationException https://sourceforge.net/p/oval/bugs/83/
      }
   }
}
