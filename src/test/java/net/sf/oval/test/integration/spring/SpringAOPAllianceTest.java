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

import static org.assertj.core.api.Assertions.*;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import net.sf.oval.constraint.MaxLength;
import net.sf.oval.constraint.NotNull;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.SuppressOValWarnings;

/**
 * @author Sebastian Thomschke
 */
public class SpringAOPAllianceTest {

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

   @Test
   public void testCGLibProxying() {
      try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringAOPAllianceTestCGLIBProxy.xml", SpringAOPAllianceTest.class)) {
         {
            final TestServiceWithoutInterface testServiceWithoutInterface = (TestServiceWithoutInterface) ctx.getBean("testServiceWithoutInterface");

            try {
               testServiceWithoutInterface.getSomething(null);
               failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
            } catch (final ConstraintsViolatedException ex) {
               assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("NOT_NULL");
            }

            try {
               testServiceWithoutInterface.getSomething("123456");
               failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
            } catch (final ConstraintsViolatedException ex) {
               assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("MAX_LENGTH");
            }
         }

         {
            final TestServiceInterface testServiceWithInterface = ctx.getBean("testServiceWithInterface", TestServiceInterface.class);

            try {
               testServiceWithInterface.getSomething(null);
               failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
            } catch (final ConstraintsViolatedException ex) {
               assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("NOT_NULL");
            }

            try {
               testServiceWithInterface.getSomething("123456");
               failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
            } catch (final ConstraintsViolatedException ex) {
               assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("MAX_LENGTH");
            }
         }
      }
   }

   @Test
   public void testJDKProxying() {
      try (ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("SpringAOPAllianceTestJDKProxy.xml", SpringAOPAllianceTest.class)) {
         final TestServiceInterface testServiceWithInterface = ctx.getBean("testServiceWithInterface", TestServiceInterface.class);

         try {
            testServiceWithInterface.getSomething(null);
            failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
         } catch (final ConstraintsViolatedException ex) {
            assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("NOT_NULL");
         }

         try {
            testServiceWithInterface.getSomething("123456");
            failBecauseExceptionWasNotThrown(ConstraintsViolatedException.class);
         } catch (final ConstraintsViolatedException ex) {
            assertThat(ex.getConstraintViolations()[0].getMessage()).isEqualTo("MAX_LENGTH");
         }
      }
   }
}
