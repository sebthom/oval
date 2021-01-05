/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;

import net.sf.oval.constraint.exclusion.Nullable;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
@SuppressWarnings("unused")
public class NullableTest {

   @Guarded(assertParametersNotNull = true)
   protected static class TestEntity1 {
      protected TestEntity1(final String param1, @Nullable final String param2) {
         // nothing
      }

      public void setParam1(final String param1) {
         // nothing
      }

      public void setParam2(@Nullable final String param2) {
         // nothing
      }
   }

   // assertParametersNotNull is false by default
   @Guarded
   protected static class TestEntity2 {
      protected TestEntity2(final String param1, @Nullable final String param2) {
         // nothing
      }

      public void setParam1(final String param1) {
         // nothing
      }

      public void setParam2(@Nullable final String param2) {
         // nothing
      }
   }

   @Test
   public void testNullable1() {
      try {
         new TestEntity1(null, "foo");
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException ex) {
         // nothing
      }

      final TestEntity1 t = new TestEntity1("foo", null);

      try {
         t.setParam1(null);
      } catch (final ConstraintsViolatedException ex) {
         // nothing
      }
      t.setParam2(null);
   }

   @Test
   public void testNullable2() {
      new TestEntity2(null, "foo");
      final TestEntity2 t = new TestEntity2("foo", null);
      t.setParam1(null);
      t.setParam2(null);
   }
}
