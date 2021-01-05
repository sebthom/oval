/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.guard;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import org.junit.Test;

import net.sf.oval.ConstraintViolation;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.Guarded;

/**
 * @author Sebastian Thomschke
 */
public class AddingChecksTest {
   @Guarded
   protected static class TestEntity1 {
      protected String name;

      protected TestEntity1(final String name) {
         this.name = name;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   @Guarded
   protected static class TestEntity2 {
      protected String name;

      protected TestEntity2(final String name) {
         this.name = name;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   @Guarded
   protected static class TestEntity3 {
      protected String name;

      protected TestEntity3(final String name) {
         this.name = name;
      }

      public void setName(final String name) {
         this.name = name;
      }
   }

   /**
    * try to programmatically add a NotNull constraint to the constructor parameter
    */
   @Test
   @SuppressWarnings("unused")
   public void testAddConstraintToConstructorParameter() throws Exception {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final Constructor<TestEntity2> constructor = TestEntity2.class.getDeclaredConstructor(String.class);
      final NotNullCheck notNullCheck = new NotNullCheck();
      notNullCheck.setMessage("NOT_NULL");

      // testing without constraint
      new TestEntity2(null);

      // adding a constraint
      guard.addChecks(constructor, 0, notNullCheck);
      try {
         new TestEntity2(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getContext()).isInstanceOf(ConstructorParameterContext.class);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
      }

      // removing the constraint
      guard.removeChecks(constructor, 0, notNullCheck);

      new TestEntity2(null);
   }

   /**
    * programmatically add a NotNull constraint to the name field
    */
   @Test
   public void testAddConstraintToField() throws Exception {
      final Guard guard = new Guard();
      TestGuardAspect.aspectOf().setGuard(guard);

      final TestEntity3 entity = new TestEntity3(null);
      assertThat(guard.validate(entity)).isEmpty();

      final Field field = TestEntity3.class.getDeclaredField("name");
      final NotNullCheck notNullCheck = new NotNullCheck();
      notNullCheck.setMessage("NOT_NULL");

      // testing without constraint
      {
         final List<ConstraintViolation> violations = guard.validate(entity);
         assertThat(violations).isEmpty();
      }

      // adding a constraint
      {
         guard.addChecks(field, notNullCheck);

         final List<ConstraintViolation> violations = TestGuardAspect.aspectOf().getGuard().validate(entity);
         assertThat(violations).hasSize(1);
         assertThat(violations.get(0).getMessage()).isEqualTo("NOT_NULL");
      }

      // removing the constraint
      {
         guard.removeChecks(field, notNullCheck);

         final List<ConstraintViolation> violations = TestGuardAspect.aspectOf().getGuard().validate(entity);
         assertThat(violations).isEmpty();
      }
   }

   /**
    * try to programmatically add a NotNull constraint to the setter parameter
    */
   @Test
   public void testAddConstraintToMethodParameter() throws NoSuchMethodException, SecurityException {
      final Guard guard = TestGuardAspect.aspectOf().getGuard();

      final Method setter = TestEntity1.class.getDeclaredMethod("setName", String.class);
      final NotNullCheck notNullCheck = new NotNullCheck();
      notNullCheck.setMessage("NOT_NULL");

      // testing without constraint
      {
         final TestEntity1 entity = new TestEntity1("blabla");
         entity.setName(null);
      }

      // adding a constraint
      guard.addChecks(setter, 0, notNullCheck);
      try {
         final TestEntity1 entity = new TestEntity1("blabla");
         entity.setName(null);
         failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
      } catch (final ConstraintsViolatedException e) {
         final ConstraintViolation[] violations = e.getConstraintViolations();
         assertThat(violations).hasSize(1);
         assertThat(violations[0].getContext()).isInstanceOf(MethodParameterContext.class);
         assertThat(violations[0].getMessage()).isEqualTo("NOT_NULL");
      }

      // removing the constraint
      guard.removeChecks(setter, 0, notNullCheck);
      {
         final TestEntity1 entity = new TestEntity1("blabla");
         entity.setName(null);
      }
   }
}
