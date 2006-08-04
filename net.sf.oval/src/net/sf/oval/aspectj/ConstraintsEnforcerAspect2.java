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

import net.sf.oval.ConstraintsEnforcer;
import net.sf.oval.ParameterNameResolverDefaultImpl;
import net.sf.oval.Validator;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This is an annotations based version of the ConstraintsEnforcerAspect aspect
 *
 * @author Sebastian Thomschke
 * @version $Revision: 1.0 $
 */
@Aspect
public abstract class ConstraintsEnforcerAspect2 extends ApiUsageAuditor2
{
	private final static Logger LOG = Logger.getLogger(ConstraintsEnforcerAspect2.class.getName());

	protected final ConstraintsEnforcer constraintsEnforcer;
	protected final Validator validator;

	public ConstraintsEnforcerAspect2()
	{
		validator = new Validator();
		constraintsEnforcer = new ConstraintsEnforcer(validator);
	}

	public ConstraintsEnforcerAspect2(ConstraintsEnforcer constraintsEnforcer)
	{
		this.constraintsEnforcer = constraintsEnforcer;
		this.validator = constraintsEnforcer.getValidator();

		// in case the this ConstraintsEnforcerAspect is used we can also use the ParameterNameResolver that utilizes the AspectJ library
		if (validator.getParameterNameResolver() instanceof ParameterNameResolverDefaultImpl)
		{
			validator.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
		}
	}

	/**
	 * constructor parameters validation
	 */
	@Around("execution((@net.sf.oval.annotations.Constrained *).new(*,..))")
	public Object constructorsWithParameter(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		constraintsEnforcer.validateConstructorParameters(TARGET, SIGNATURE.getConstructor(),
				parameterValues);

		return thisJoinPoint.proceed();
	}

	/**
	 * method parameters validation
	 */
	@Around("execution(* (@net.sf.oval.annotations.Constrained *).*(*,..))")
	public Object methodsWithParameter(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		final boolean valid = constraintsEnforcer.validateMethodParameters(TARGET, SIGNATURE
				.getMethod(), parameterValues);

		return valid ? thisJoinPoint.proceed() : null;
	}

	/**
	 * object validation before method execution
	 */
	@Around("execution(@net.sf.oval.annotations.PreValidateThis * (@Constrained *).*(..))")
	public Object methodsPreValidateThis(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final boolean valid = constraintsEnforcer.validate(TARGET, false);

		return valid ? thisJoinPoint.proceed() : null;
	}

	/**
	 * method return value validation
	 */
	@Around("execution(* (@net.sf.oval.annotations.Constrained *).*(..))")
	public Object methodsWithReturnValue(final ProceedingJoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object returnValue = thisJoinPoint.proceed();

		constraintsEnforcer.validateMethodReturnValue(TARGET, SIGNATURE.getMethod(), returnValue);

		return returnValue;
	}

	/**
	 * object validation after constructor execution
	 */
	@AfterReturning("execution(@net.sf.oval.annotations.PostValidateThis (@net.sf.oval.annotations.Constrained *).new(..))")
	public void constructorsPostValidateThis(final JoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		constraintsEnforcer.validate(TARGET, true);
	}

	/**
	 * object validation after method execution
	 */
	@AfterReturning("execution(@net.sf.oval.annotations.PostValidateThis * (@net.sf.oval.annotations.Constrained *).*(..))")
	public void methodsPostValidateThis(final JoinPoint thisJoinPoint)
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		constraintsEnforcer.validate(TARGET, false);
	}

	/**
	 * @return the constraintsEnforcer
	 */
	public ConstraintsEnforcer getConstraintsEnforcer()
	{
		return constraintsEnforcer;
	}

	/**
	 * @return the validator
	 */
	public Validator getValidator()
	{
		return validator;
	}
}
