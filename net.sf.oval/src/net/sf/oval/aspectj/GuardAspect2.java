/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.aspectj;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.Guard;
import net.sf.oval.IsGuarded;
import net.sf.oval.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.DeclareParents;
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
	@DeclareParents("(@net.sf.oval.annotations.Guarded *)")
	private IsGuarded implementedInterface;

	private Guard guard;
	private Validator validator;

	public GuardAspect2()
	{
		this(new Guard(new Validator()));
	}

	public GuardAspect2(final Guard guard)
	{
		LOG.info("Instantiated");

		setGuard(guard);
	}

	/**
	 * object validation after constructor execution
	 */
	@AfterReturning("execution(@net.sf.oval.annotations.PostValidateThis (@net.sf.oval.annotations.Guarded *).new(..))")
	public void constructorsPostValidateThis(final JoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		guard.validate(TARGET, true);
	}

	/**
	 * constructor parameters validation
	 */
	@Around("execution((@net.sf.oval.annotations.Guarded *).new(*,..))")
	public Object constructorsWithParameter(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		guard.validateConstructorParameters(TARGET, SIGNATURE.getConstructor(), parameterValues);

		return thisJoinPoint.proceed();
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

	/**
	 * object validation after method execution
	 */
	@AfterReturning("execution(@net.sf.oval.annotations.PostValidateThis * (@net.sf.oval.annotations.Guarded *).*(..))")
	public void methodsPostValidateThis(final JoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		guard.validate(TARGET, false);
	}

	/**
	 * object validation before method execution
	 */
	@Around("execution(@net.sf.oval.annotations.PreValidateThis * (@net.sf.oval.annotations.Guarded *).*(..))")
	public Object methodsPreValidateThis(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final boolean valid = guard.validate(TARGET, false);

		return valid ? thisJoinPoint.proceed() : null;
	}

	/**
	 * method parameters validation
	 */
	@Around("execution(* (@net.sf.oval.annotations.Guarded *).*(*,..))")
	public Object methodsWithParameter(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		final boolean valid = guard.validateMethodParameters(TARGET, SIGNATURE.getMethod(),
				parameterValues);

		return valid ? thisJoinPoint.proceed() : null;
	}

	/**
	 * method return value validation
	 */
	@Around("execution(* (@net.sf.oval.annotations.Guarded *).*(..))")
	public Object methodsWithReturnValue(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object returnValue = thisJoinPoint.proceed();

		guard.validateMethodReturnValue(TARGET, SIGNATURE.getMethod(), returnValue);

		return returnValue;
	}

	public final void setGuard(final Guard guard)
	{
		this.guard = guard;
		this.validator = guard.getValidator();
	}
}
