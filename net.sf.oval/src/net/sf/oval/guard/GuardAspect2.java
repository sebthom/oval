/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.guard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.oval.exception.ValidationFailedException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.Invocable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This is an annotations based version of the GuardAspect aspect.
 * 
 * In contrast to GuardAspect no custom scopes are supported yet, 
 * so only guarding based on the @Guarded annotation is possible right now.
 *
 * To workaround an AspectJ bug use the -XnoInline weave option, in case you are getting errors like:
 * java.lang.VerifyError: (class: net/sf/oval/guard/GuardAspect2, method: ajc$inlineAccessMethod$net_sf_oval_guard_GuardAspect2$net_sf_oval_guard_Guard$guardMethodPost signature: (Lnet/sf/oval/guard/Guard;Ljava/lang/Object;Ljava/lang/reflect/Method;[Ljava/lang/Object;Ljava/lang/Object;)V) Illegal use of nonvirtual function call
 *
 * @author Sebastian Thomschke
 */
@Aspect
public abstract class GuardAspect2 extends ApiUsageAuditor2
{
	private final static Log LOG = Log.getLog(GuardAspect2.class);

	@SuppressWarnings("unused")
	// add the IsGuarded marker interface to all classes annotated with @Guarded
	@DeclareParents("(@net.sf.oval.guard.Guarded *)")
	private IsGuarded implementedInterface;

	private Guard guard;

	public GuardAspect2()
	{
		this(new Guard());
		getGuard().setParameterNameResolver(new ParameterNameResolverAspectJImpl());
	}

	public GuardAspect2(final Guard guard)
	{
		LOG.info("Instantiated");

		setGuard(guard);
	}

	@Around("execution((@net.sf.oval.guard.Guarded *).new(..))")
	public Object allConstructors(final ProceedingJoinPoint thisJoinPoint) throws Throwable
	{
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		LOG.debug("aroundConstructor() {}", SIGNATURE);

		final Constructor CONSTRUCTOR = SIGNATURE.getConstructor();
		final Object[] args = thisJoinPoint.getArgs();
		final Object TARGET = thisJoinPoint.getTarget();

		// pre conditions
		{
			guard.guardConstructorPre(TARGET, CONSTRUCTOR, args);
		}

		final Object result = thisJoinPoint.proceed();

		// post conditions
		{
			guard.guardConstructorPost(TARGET, CONSTRUCTOR, args);
		}

		return result;
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	@Around("execution(* (@net.sf.oval.guard.Guarded *).*(..))")
	public Object allMethods(final ProceedingJoinPoint thisJoinPoint) throws Throwable
	{
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		LOG.debug("aroundMethod() {}", SIGNATURE);

		final Method METHOD = SIGNATURE.getMethod();
		final Object[] args = thisJoinPoint.getArgs();
		final Object TARGET = thisJoinPoint.getTarget();

		return guard.guardMethod(TARGET, METHOD, args, new Invocable()
			{
				public Object invoke()
				{
					try
					{
						return thisJoinPoint.proceed();
					}
					catch (final Throwable ex)
					{
						throw new ValidationFailedException(
								"Unexpected exception while invoking method.", ex);
					}
				}
			});
	}

	/**
	 * @return the guard
	 */
	public Guard getGuard()
	{
		return guard;
	}

	public final void setGuard(final Guard guard)
	{
		this.guard = guard;
	}
}
