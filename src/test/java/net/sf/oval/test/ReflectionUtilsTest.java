package net.sf.oval.test;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

import junit.framework.TestCase;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Gary Madden
 */
public class ReflectionUtilsTest extends TestCase {
   public void testGetInterfaceMethods() throws NoSuchMethodException {
      List<Method> methods = ReflectionUtils.getInterfaceMethods(Interface.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertTrue(methods.isEmpty());

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertEquals(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")), methods);

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doIt"),
              Collections.emptySet(), Collections.singleton(Interface.class));
      assertTrue(methods.isEmpty());

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doIt"),
              Collections.singleton(Interface.class), Collections.emptySet());
      assertEquals(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")), methods);

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doSomethingElse"), Collections.emptySet(), Collections.emptySet());
      assertTrue(methods.isEmpty());

      methods = ReflectionUtils.getInterfaceMethods(Implementation.class.getDeclaredMethod("doAnotherThing"), Collections.emptySet(), Collections.emptySet());
      assertTrue(methods.isEmpty());

      methods = ReflectionUtils.getInterfaceMethods(ImplementationChild.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertEquals(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")), methods);

      methods = ReflectionUtils.getInterfaceMethods(ImplementationChild.class.getDeclaredMethod("doIt"),
              Collections.emptySet(), Collections.singleton(Interface.class));
      assertTrue(methods.isEmpty());

      methods = ReflectionUtils.getInterfaceMethods(ImplementationChild.class.getDeclaredMethod("doIt"),
              Collections.singleton(Interface.class), Collections.emptySet());
      assertEquals(Collections.singletonList(Interface.class.getDeclaredMethod("doIt")), methods);

      methods = ReflectionUtils.getInterfaceMethods(OtherClass.class.getDeclaredMethod("doIt"), Collections.emptySet(), Collections.emptySet());
      assertTrue(methods.isEmpty());
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
