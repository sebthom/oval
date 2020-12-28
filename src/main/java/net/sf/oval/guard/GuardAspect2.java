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
import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Invocable;

/**
 * This is an annotations based version of the GuardAspect aspect.
 *
 * In contrast to GuardAspect no custom scopes are supported yet,
 * so only guarding based on the @Guarded annotation is possible right now.
 *
 * To workaround an AspectJ bug use the -XnoInline weave option, in case you are getting errors like:
 * java.lang.VerifyError: (class: net/sf/oval/guard/GuardAspect2, method:
 * ajc$inlineAccessMethod$net_sf_oval_guard_GuardAspect2$net_sf_oval_guard_Guard$guardMethodPost signature:
 * (Lnet/sf/oval/guard/Guard;Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;Ljava/lang/Object;)V) Illegal use of nonvirtual function call
 *
 * @author Sebastian Thomschke
 */
@Aspect
public abstract class GuardAspect2 extends ApiUsageAuditor2 {
   private static final class ProceedInvocable implements Invocable<Object, Throwable> {
      final ProceedingJoinPoint thisJoinPoint;

      protected ProceedInvocable(final ProceedingJoinPoint thisJoinPoint) {
         this.thisJoinPoint = thisJoinPoint;
      }

      @Override
      public Object invoke() throws Throwable {
         return thisJoinPoint.proceed();
      }
   }

   private static final Log LOG = Log.getLog(GuardAspect2.class);

   // add the IsGuarded marker interface to all classes annotated with @Guarded
   @DeclareParents("(@net.sf.oval.guard.Guarded *)")
   private IsGuarded implementedInterface;

   private Guard guard;

   /**
    * Constructor instantiating a new Guard object.
    */
   public GuardAspect2() {
      this(new Guard());
      getGuard().setParameterNameResolver(new ParameterNameResolverAspectJImpl());
   }

   /**
    * Constructor using the given Guard object
    *
    * @param guard the guard to use
    */
   public GuardAspect2(final Guard guard) {
      LOG.info("Instantiated");

      setGuard(guard);
   }

   @Around("execution((@net.sf.oval.guard.Guarded *).new(..))")
   public Object allConstructors(final ProceedingJoinPoint thisJoinPoint) throws Throwable { // CHECKSTYLE:IGNORE IllegalThrow
      final ConstructorSignature signature = (ConstructorSignature) thisJoinPoint.getSignature();

      LOG.debug("aroundConstructor() {1}", signature);

      final Constructor<?> ctor = signature.getConstructor();
      final Object[] args = thisJoinPoint.getArgs();
      final Object target = thisJoinPoint.getTarget();

      // pre conditions
      {
         guard.guardConstructorPre(target, ctor, args);
      }

      final Object result = thisJoinPoint.proceed();

      // post conditions
      {
         guard.guardConstructorPost(target, ctor, args);
      }

      return result;
   }

   @SuppressAjWarnings("adviceDidNotMatch")
   @Around("execution(* (@net.sf.oval.guard.Guarded *).*(..))")
   public Object allMethods(final ProceedingJoinPoint thisJoinPoint) throws Throwable { // CHECKSTYLE:IGNORE IllegalThrow
      final MethodSignature signature = (MethodSignature) thisJoinPoint.getSignature();

      LOG.debug("aroundMethod() {1}", signature);

      final Method method = signature.getMethod();
      final Object[] args = thisJoinPoint.getArgs();
      final Object target = thisJoinPoint.getTarget();

      return guard.guardMethod(target, method, args, new ProceedInvocable(thisJoinPoint));
   }

   public Guard getGuard() {
      return guard;
   }

   public final void setGuard(final Guard guard) {
      this.guard = guard;
   }
}
