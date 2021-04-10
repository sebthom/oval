/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.ExceptionTranslatorJDKExceptionsImpl;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class ExceptionTranslatorTest {

   @Guarded
   public static final class TestEntity {
      public void setName(@SuppressWarnings("unused") @NotNull(message = "NULL") final String name) {
         //...
      }

      public void throwCheckedException() throws InvocationTargetException {
         throw new InvocationTargetException(null);
      }
   }

   @Test
   public void testExceptionTranslator() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      assertThat(guard.getExceptionTranslator()).isNull();

      try {
         final TestEntity t = new TestEntity();
         t.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         assertThat(ex.getMessage()).isEqualTo("NULL");
      }

      try {
         final TestEntity t = new TestEntity();

         guard.setExceptionTranslator(new ExceptionTranslatorJDKExceptionsImpl());
         try {
            t.setName(null);
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
         } catch (final IllegalArgumentException ex) {
            assertThat(ex.getMessage()).isEqualTo("NULL");
         }

         try {
            t.throwCheckedException();
            failBecauseExceptionWasNotThrown(InvocationTargetException.class);
         } catch (final InvocationTargetException ex) {
            // expected
         }

      } finally {
         guard.setExceptionTranslator(null);
      }
   }
}
