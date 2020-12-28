/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import java.lang.reflect.Constructor;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Invocable;

/**
 * AOP Alliance Interceptor implementation of the Guard aspect
 *
 * @author Sebastian Thomschke
 */
public class GuardInterceptor implements MethodInterceptor, ConstructorInterceptor {
   protected static final class MethodInvocable implements Invocable<Object, Throwable> {
      private final MethodInvocation methodInvocation;

      protected MethodInvocable(final MethodInvocation methodInvocation) {
         this.methodInvocation = methodInvocation;
      }

      @Override
      public Object invoke() throws Throwable {
         return methodInvocation.proceed();
      }
   }

   private static final Log LOG = Log.getLog(GuardInterceptor.class);

   private Guard guard;

   public GuardInterceptor() {
      this(new Guard());
   }

   public GuardInterceptor(final Guard guard) {
      LOG.info("Instantiated");

      setGuard(guard);
   }

   @Override
   public Object construct(final ConstructorInvocation constructorInvocation) throws Throwable {
      final Constructor<?> ctor = constructorInvocation.getConstructor();
      final Object[] args = constructorInvocation.getArguments();
      final Object target = constructorInvocation.getThis();

      // pre conditions
      guard.guardConstructorPre(target, ctor, args);

      final Object result = constructorInvocation.proceed();

      // post conditions
      guard.guardConstructorPost(target, ctor, args);

      return result;
   }

   public Guard getGuard() {
      return guard;
   }

   @Override
   public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
      return guard.guardMethod(methodInvocation.getThis(), methodInvocation.getMethod(), methodInvocation.getArguments(), new MethodInvocable(
         methodInvocation));
   }

   public void setGuard(final Guard guard) {
      this.guard = guard;
   }
}
