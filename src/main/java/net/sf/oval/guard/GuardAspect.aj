/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
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

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

import net.sf.oval.internal.Log;

/**
 * This aspect intercepts calls to constructors and methods annotated with @net.sf.oval.annotations.Guarded
 * for automatic runtime validation of constraints defined for constructor/method parameters and method return values.
 * 
 * @author Sebastian Thomschke
 */
public abstract aspect GuardAspect extends ApiUsageAuditor {
    private final static Log LOG = Log.getLog(GuardAspect.class);

    private final static ParameterNameResolverAspectJImpl PARAMETER_NAME_RESOLVER = new ParameterNameResolverAspectJImpl();

    private Guard guard;

    public GuardAspect() {
        this(new Guard());
        getGuard().setParameterNameResolver(PARAMETER_NAME_RESOLVER);
    }

    public GuardAspect(final Guard guard) {
        LOG.info("Instantiated");

        setGuard(guard);
    }

    /**
     * @return the guard
     */
    public Guard getGuard() {
        return guard;
    }

    public void setGuard(final Guard guard) {
        this.guard = guard;
        getGuard().setParameterNameResolver(PARAMETER_NAME_RESOLVER);
    }

    /* ****************
     * POINT CUTS
     * ****************/

    // the scope of the aspect are all classes annotated with @Guarded
    protected pointcut scope(): @within(Guarded);

    private pointcut allConstructors() : execution(*.new(..));

    private pointcut allMethods() : execution(* *.*(..));

    /* ****************
     * ADVICES
     * ****************/

    // add the IsGuarded marker interface to all classes annotated with @Guarded
    declare parents: (@Guarded *) implements IsGuarded;

    @SuppressAjWarnings("adviceDidNotMatch")
    Object around(): scope() && allConstructors() {
        final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

        LOG.debug("aroundCounstructor() {1}", SIGNATURE);

        final Constructor<?> CONSTRUCTOR = SIGNATURE.getConstructor();
        final Object[] args = thisJoinPoint.getArgs();
        final Object TARGET = thisJoinPoint.getTarget();

        // pre conditions
        getGuard().guardConstructorPre(TARGET, CONSTRUCTOR, args);

        final Object result = proceed();

        // post conditions
        getGuard().guardConstructorPost(TARGET, CONSTRUCTOR, args);

        return result;
    }

    @SuppressAjWarnings("adviceDidNotMatch")
    Object around(): scope() && allMethods() {
        final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

        LOG.debug("aroundMethod() {1}", SIGNATURE);

        final Method METHOD = SIGNATURE.getMethod();
        final Object[] args = thisJoinPoint.getArgs();
        final Object TARGET = thisJoinPoint.getTarget();

        /* Because of a limitation in AspectJ the following does not compile currently. See https://bugs.eclipse.org/bugs/show_bug.cgi?id=240608
            return guard.guardMethod(TARGET, METHOD, args, new Invocable() {
                public Object invoke() throws Throwable {
                    // invoke the advised method and return the result
                    return proceed();
                }
            });
         */

        // pre conditions
        final Guard.GuardMethodPreResult preResult = getGuard().guardMethodPre(TARGET, METHOD, args);
        if(preResult == Guard.DO_NOT_PROCEED) {
            LOG.debug("not proceeding with method execution");
            return null;
        }

        final Object result = proceed();

        // post conditions
        getGuard().guardMethodPost(result, preResult);

        return result;
    }
}
