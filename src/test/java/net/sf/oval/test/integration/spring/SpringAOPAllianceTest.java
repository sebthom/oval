/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.test.integration.spring;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;
import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.SuppressOValWarnings;

/**
 * @author Sebastian Thomschke
 */
public class SpringAOPAllianceTest extends TestCase {
   public interface TestServiceInterface {
      @MaxLength(value = 5, message = "MAX_LENGTH")
      String getSomething(@NotNull(message = "NOT_NULL") String input);
   }

   /**
    * interface based service
    */
   @Guarded(inspectInterfaces = true)
   public static class TestServiceWithInterface implements TestServiceInterface {
      @Override
      public String getSomething(final String input) {
         return input;
      }
   }

   /**
    * class based service
    */
   public static class TestServiceWithoutInterface {
      @SuppressOValWarnings
      @MaxLength(value = 5, message = "MAX_LENGTH")
      public String getSomething(@NotNull(message = "NOT_NULL") final String input) {
         return input;
      }
   }

   public void testCGLibProxying() {
      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringAOPAllianceTestCGLIBProxy.xml", SpringAOPAllianceTest.class);

      try {
         {
            final TestServiceWithoutInterface testServiceWithoutInterface = (TestServiceWithoutInterface) ctx.getBean("testServiceWithoutInterface");

            try {
               testServiceWithoutInterface.getSomething(null);
               fail();
            } catch (final ConstraintsViolatedException ex) {
               assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
            }

            try {
               testServiceWithoutInterface.getSomething("123456");
               fail();
            } catch (final ConstraintsViolatedException ex) {
               assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
            }
         }

         {
            final TestServiceInterface testServiceWithInterface = ctx.getBean("testServiceWithInterface", TestServiceInterface.class);

            try {
               testServiceWithInterface.getSomething(null);
               fail();
            } catch (final ConstraintsViolatedException ex) {
               assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
            }

            try {
               testServiceWithInterface.getSomething("123456");
               fail();
            } catch (final ConstraintsViolatedException ex) {
               assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
            }
         }
      } finally {
         ctx.close();
      }
   }

   public void testJDKProxying() {
      final ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringAOPAllianceTestJDKProxy.xml", SpringAOPAllianceTest.class);

      try {
         final TestServiceInterface testServiceWithInterface = ctx.getBean("testServiceWithInterface", TestServiceInterface.class);

         try {
            testServiceWithInterface.getSomething(null);
            fail();
         } catch (final ConstraintsViolatedException ex) {
            assertEquals("NOT_NULL", ex.getConstraintViolations()[0].getMessage());
         }

         try {
            testServiceWithInterface.getSomething("123456");
            fail();
         } catch (final ConstraintsViolatedException ex) {
            assertEquals("MAX_LENGTH", ex.getConstraintViolations()[0].getMessage());
         }
      } finally {
         ctx.close();
      }
   }
}
