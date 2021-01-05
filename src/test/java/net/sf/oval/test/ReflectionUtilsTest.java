/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test;

import static org.assertj.core.api.Assertions.*;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Gary Madden
 */
public class ReflectionUtilsTest {

   @Test
   public void testGetInterfaceMethods() throws NoSuchMethodException {
      List<Method> methods = ReflectionUtils.getInterfaceMethods(Interface.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertThat(methods.isEmpty()).isTrue();

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertThat(methods).isEqualTo(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")));

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.singleton(
         Interface.class));
      assertThat(methods.isEmpty()).isTrue();

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doIt"), Collections.singleton(Interface.class), Collections
         .emptySet());
      assertThat(methods).isEqualTo(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")));

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doSomethingElse"), Collections.emptySet(), Collections.emptySet());
      assertThat(methods.isEmpty()).isTrue();

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doAnotherThing"), Collections.emptySet(), Collections.emptySet());
      assertThat(methods.isEmpty()).isTrue();

      methods = ReflectionUtils.getInterfaceMethods(ImplementationChild.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertThat(methods).isEqualTo(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")));

      methods = ReflectionUtils.getInterfaceMethods(ImplementationChild.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.singleton(
         Interface.class));
      assertThat(methods.isEmpty()).isTrue();

      methods = ReflectionUtils.getInterfaceMethods(ImplementationChild.class.getDeclaredMethod("doIt"), Collections.singleton(Interface.class), Collections
         .emptySet());
      assertThat(methods).isEqualTo(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")));

      methods = ReflectionUtils.getInterfaceMethods(OtherClass.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertThat(methods.isEmpty()).isTrue();
   }

   interface Interface {
      void doIt();
   }

   static class Implementation implements Interface {
      @Override
      public void doIt() {
      }

      public void doSomethingElse() {
      }

      static void doAnotherThing() {
      }
   }

   static class ImplementationChild extends Implementation {
      @Override
      public void doIt() {
      }
   }

   static class OtherClass {
      public void doIt() {
      }
   }
}
