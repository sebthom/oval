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
import net.sf.oval.constraint.AssertFieldConstraints;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class InheritanceTest extends TestCase {
   @Guarded
   public static class Entity extends SuperEntity {
      /**
       * @param name the name to set
       */
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

      /**
       * @return the name
       */
      public String getName() {
         return name;
      }

      /**
       * @param name the name to set
       */
      @Override
      public void setName(final String name) {
         this.name = name;
      }
   }

   @Guarded
   public static class EntityWithInterfaceButUnapplied implements EntityInterface {
      protected String name = "";

      /**
       * @return the name
       */
      public String getName() {
         return name;
      }

      /**
       * @param name the name to set
       */
      @Override
      public void setName(final String name) {
         this.name = name;
      }
   }

   @Guarded(applyFieldConstraintsToSetters = true)
   public static class SuperEntity {
      @NotNull
      protected String name = "";

      /**
       * @return the name
       */
      public String getName() {
         return name;
      }

      /**
       * @param name the name to set
       */
      public void setName(final String name) {
         this.name = name;
      }
   }

   public void testInheritance() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Entity e = new Entity();

      try {
         e.setName(null);
         fail("ConstraintViolationException should have been thrown");
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }

      try {
         e.setName2(null);
         fail("ConstraintViolationException should have been thrown");
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   public void testInterface() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final EntityWithInterface e = new EntityWithInterface();

      try {
         e.setName(null);
         fail("ConstraintViolationException should have been thrown");
      } catch (final ConstraintsViolatedException ex) {
         // expected
      }
   }

   public void testInterfaceNotApplied() {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final EntityWithInterfaceButUnapplied e = new EntityWithInterfaceButUnapplied();

      try {
         e.setName(null);
      } catch (final ConstraintsViolatedException ex) {
         fail("ConstraintViolationException should not have been thrown");
      }
   }
}
