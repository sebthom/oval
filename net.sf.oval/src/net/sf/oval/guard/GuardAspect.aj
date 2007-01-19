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
package net.sf.oval.guard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.Validator;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This aspect intercepts calls to constructors and methods annotated with @net.sf.oval.annotations.Guarded 
 * for automatic runtime validation of constraints defined for constructor/method parameters and method return values.
 * 
 * @author Sebastian Thomschke
 */
public abstract aspect GuardAspect extends ApiUsageAuditor
{
	private final static Logger LOG = Logger.getLogger(GuardAspect.class.getName());

	private Guard guard;
	private Validator validator;

	public GuardAspect()
	{
		this(new Guard(new Validator()));
	}

	public GuardAspect(final Guard guard)
	{
		LOG.info("Instantiated");

		setGuard(guard);
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

	public void setGuard(final Guard guard)
	{
		this.guard = guard;
		this.validator = guard.getValidator();
	}

	/*
	 * POINT CUTS
	 */
	protected pointcut scope(): @within(Guarded);

	private pointcut allConstructors() : execution(*.new(..));

	private pointcut allMethods() : execution(* *.*(..));

	/*
	 * ADVICES
	 */
	declare parents: (@Guarded *) implements IsGuarded;

	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): scope() && allMethods()
	{
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Method METHOD = SIGNATURE.getMethod();
		final Object[] args = thisJoinPoint.getArgs();
		final Object TARGET = thisJoinPoint.getTarget();

		// pre conditions
		{
			final boolean valid = guard.guardMethodPre(TARGET, METHOD, args);
			if (!valid) return null; // this happens in listener mode
		}

		final Object result = proceed();

		// post conditions
		{
			final boolean valid = guard.guardMethodPost(TARGET, METHOD, args, result);
			if (!valid) return null; // this happens in listener mode
		}

		return result;
	}

	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): scope() && allConstructors()
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

		final Object result = proceed();

		// @PostValidateThis
		if (CONSTRUCTOR.isAnnotationPresent(PostValidateThis.class))
		{
			guard.guardConstructorPost(TARGET, CONSTRUCTOR, args);
		}

		return result;
	}
}
