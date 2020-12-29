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

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;

import net.sf.oval.configuration.annotation.BeanValidationAnnotationsConfigurer;
import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.guard.Guard;
import net.sf.oval.guard.GuardInterceptor;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.SuppressOValWarnings;

/**
 * @author Sebastian Thomschke
 */
public class SpringAOPAllianceBeanValidationTest {

   public interface TestServiceInterface {
      @Size(max = 5, message = "MAX_LENGTH")
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
      @Size(max = 5, message = "MAX_LENGTH")
      public String getSomething(@NotNull(message = "NOT_NULL") final String input) {
         return input;
      }
   }

   @Test
   public void testCGLibProxying() {
      {
         final ProxyFactory prFactory = new ProxyFactory(new TestServiceWithoutInterface());
         prFactory.setProxyTargetClass(true);
         prFactory.addAdvice(new GuardInterceptor(new Guard(new BeanValidationAnnotationsConfigurer())));
         final TestServiceWithoutInterface testServiceWithoutInterface = (TestServiceWithoutInterface) prFactory.getProxy();

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
         final ProxyFactory prFactory = new ProxyFactory(new TestServiceWithInterface());
         prFactory.setProxyTargetClass(true);
         prFactory.addAdvice(new GuardInterceptor(new Guard(new BeanValidationAnnotationsConfigurer())));
         final TestServiceWithInterface testServiceWithInterface = (TestServiceWithInterface) prFactory.getProxy();

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

   @Test
   public void testJDKProxying() {
      final ProxyFactory prFactory = new ProxyFactory(new TestServiceWithInterface());
      prFactory.setProxyTargetClass(false);
      prFactory.addAdvice(new GuardInterceptor(new Guard(new BeanValidationAnnotationsConfigurer())));
      final TestServiceInterface testServiceWithInterface = (TestServiceInterface) prFactory.getProxy();

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
