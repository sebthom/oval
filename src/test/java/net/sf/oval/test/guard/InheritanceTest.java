/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import javax.validation.ConstraintViolationException;

import org.junit.Test;

import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class InheritanceTest {

   @Guarded
   public static class Entity extends SuperEntity {
      public void setName2(@AssertFieldConstraints final String name) {
         this.name = name;
      }
   }

   public interface EntityInterface {
      void setName(@NotNull String name);
   }

   @Guarded(inspectInterfaces = true)
   public static class EntityWithInterface implements EntityInterface {
      protected String name = "";

      public String getName() {
         return name;
      }

      @Override
      public void setName(final String name) {
         this.name = name;
      }
   }

   @Guarded(inspectInterfaces = false)
   public static class EntityWithInterfaceButUnapplied implements EntityInterface {
      protected String name = "";

      public String getName() {
         return name;
      }

      @Override
      public void setName(final String name) {
         this.name = name;
      }
   }

   @Guarded(applyFieldConstraintsToSetters = true)
   public static class SuperEntity {
      @NotNull
      protected String name = "";

      public String getName() {
         return name;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   @Test
   public void testInheritance() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Entity e = new Entity();

      try {
         e.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }

      try {
         e.setName2(null);
         failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   @Test
   public void testInterface() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final EntityWithInterface e = new EntityWithInterface();

      try {
         e.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintViolationException.class);
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   @Test
   public void testInterfaceNotApplied() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final EntityWithInterfaceButUnapplied e = new EntityWithInterfaceButUnapplied();

      // should not throw ConstraintsViolatedException
      e.setName(null);
   }
}
