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
import net.sf.oval.annotations.Guarded;
import net.sf.oval.annotations.PostValidateThis;
import net.sf.oval.annotations.PreValidateThis;

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

	/*
	 pointcut constructorsPostValidateThis(): execution(@PostValidateThis (@Constrained *).new(..));
	 pointcut constructorsWithParameter(): execution((@Constrained *).new(*,..));
	 pointcut methodsWithParameter() : execution(* (@Constrained *).*(*,..));
	 pointcut methodsWithReturnValue() : execution(* (@Constrained *).*(..));
	 pointcut methodsPostValidateThis(): execution(@PostValidateThis * (@Constrained *).*(..));
	 pointcut methodsPreValidateThis(): execution(@PreValidateThis * (@Constrained *).*(..));
	 declare parents: (@Constrained *) implements Guarded;
	 */

	protected pointcut scope(): @within(Guarded);

	private pointcut constructorsPostValidateThis(): execution(@PostValidateThis *.new(..));

	private pointcut constructorsWithParameter(): execution(*.new(*,..));

	private pointcut methodsWithReturnValue() : execution(* *.*(..));

	private pointcut methodsWithParameter() : execution(* *.*(*,..));

	private pointcut methodsPostValidateThis(): execution(@PostValidateThis * *.*(..));

	private pointcut methodsPreValidateThis(): execution(@PreValidateThis * *.*(..));

	/*
	 * ADVICES
	 */
	declare parents: (@Guarded *) implements IsGuarded;
	
	/**
	 * constructor parameters validation
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): scope() && constructorsWithParameter()
	 {
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		guard.validateConstructorParameters(TARGET, SIGNATURE.getConstructor(),
				parameterValues);

		return proceed();
	}

	/**
	 * method parameters validation
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): scope() && methodsWithParameter()
	 {
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		final boolean valid = guard.validateMethodParameters(TARGET, SIGNATURE
				.getMethod(), parameterValues);

		return valid ? proceed() : null;
	}

	/**
	 * object validation before method execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): scope() && methodsPreValidateThis()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final boolean valid = guard.validate(TARGET, false);

		return valid ? proceed() : null;
	}

	/**
	 * method return value validation
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): scope() && methodsWithReturnValue()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object returnValue = proceed();

		guard.validateMethodReturnValue(TARGET, SIGNATURE.getMethod(), returnValue);

		return returnValue;
	}

	/**
	 * object validation after constructor execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	after() returning: scope() && constructorsPostValidateThis()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		guard.validate(TARGET, true);
	}

	/**
	 * object validation after method execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	after() returning: scope() && methodsPostValidateThis()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		guard.validate(TARGET, false);
	}
}
