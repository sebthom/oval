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
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.ParameterNameResolverAspectJImpl;
import net.sf.oval.Validator;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This is an annotations based version of the GuardAspect aspect
 *
 * @author Sebastian Thomschke
 * 
 * @see Guard
 */
@Aspect
public abstract class GuardAspect2 extends ApiUsageAuditor2
{
	private final static Logger LOG = Logger.getLogger(GuardAspect2.class.getName());

	@SuppressWarnings("unused")
	@DeclareParents("(@net.sf.oval.guard.Guarded *)")
	private IsGuarded implementedInterface;

	private Guard guard;
	private Validator validator;

	public GuardAspect2()
	{
		this(new Guard(new Validator()));
		getValidator().setParameterNameResolver(new ParameterNameResolverAspectJImpl());
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

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

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

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Method METHOD = SIGNATURE.getMethod();
		final Object[] args = thisJoinPoint.getArgs();
		final Object TARGET = thisJoinPoint.getTarget();

		// pre conditions
		{
			final boolean valid = guard.guardMethodPre(TARGET, METHOD, args);
			if (!valid) return null; // this happens if swallow exceptions mode is enabled for this guarded object or class
		}

		final Object result = thisJoinPoint.proceed();

		// post conditions
		{
			guard.guardMethodPost(TARGET, METHOD, args, result);
		}

		return result;
	}

	/**
	 * @return the guard
	 */
	public Guard getGuard()
	{
		return guard;
	}

	/**
	 * @return the validator
	 */
	public Validator getValidator()
	{
		return validator;
	}

	public final void setGuard(final Guard guard)
	{
		this.guard = guard;
		this.validator = guard.getValidator();
	}
}
