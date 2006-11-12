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
import net.sf.oval.ParameterNameResolverEnumerationImpl;
import net.sf.oval.Validator;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.PostValidateThis;
import net.sf.oval.annotations.PreValidateThis;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This aspect intercepts calls to constructors and methods annotated with @net.sf.oval.annotations.Constrained 
 * for automatic runtime validation of constraints defined for constructor/method parameters and method return values.
 * 
 * @author Sebastian Thomschke
 */
public abstract aspect ConstraintsEnforcerAspect extends ApiUsageAuditor
{
	private final static Logger LOG = Logger.getLogger(ConstraintsEnforcerAspect.class.getName());

	protected final ConstraintsEnforcer constraintsEnforcer;
	protected final Validator validator;

	public ConstraintsEnforcerAspect()
	{
		validator = new Validator();
		constraintsEnforcer = new ConstraintsEnforcer(validator);
	}

	public ConstraintsEnforcerAspect(final ConstraintsEnforcer constraintsEnforcer)
	{
		LOG.info("Instantiated");

		this.constraintsEnforcer = constraintsEnforcer;
		this.validator = constraintsEnforcer.getValidator();

		// in case the this ConstraintsEnforcerAspect is used we can also use the ParameterNameResolver that utilizes the AspectJ library
		if (validator.getParameterNameResolver() instanceof ParameterNameResolverEnumerationImpl)
		{
			validator.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
		}
	}

	/*
	 * POINT CUTS
	 */
	pointcut constructorsPostValidateThis(): execution(@PostValidateThis (@Constrained *).new(..));

	pointcut constructorsWithParameter(): execution((@Constrained *).new(*,..));

	pointcut methodsWithParameter() : execution(* (@Constrained *).*(*,..));

	pointcut methodsWithReturnValue() : execution(* (@Constrained *).*(..));

	pointcut methodsPostValidateThis(): execution(@PostValidateThis * (@Constrained *).*(..));

	pointcut methodsPreValidateThis(): execution(@PreValidateThis * (@Constrained *).*(..));

	/*
	 pointcut constrainedClasses(): @target(Constrained);
	 pointcut constructorsPostValidateObject(): constrainedClasses() && execution(@PostValidateObject *.new(..));
	 pointcut constructorsWithParameter(): constrainedClasses() && execution(*.new(*,..));
	 pointcut methodsWithParameter() : constrainedClasses() && execution(* *.*(*,..));
	 pointcut methodsPostValidateObject(): constrainedClasses() && execution(@PostValidateObject * *.*(..));
	 pointcut methodsPreValidateObject(): constrainedClasses() && execution(@PreValidateObject * *.*(..));
	 */

	/*
	 * ADVICES
	 */
	declare parents: (@Constrained *) implements ConstraintsEnforcementIsEnabled;

	/**
	 * constructor parameters validation
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): constructorsWithParameter()
	 {
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		constraintsEnforcer.validateConstructorParameters(TARGET, SIGNATURE.getConstructor(),
				parameterValues);

		return proceed();
	}

	/**
	 * method parameters validation
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): methodsWithParameter()
	 {
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object[] parameterValues = thisJoinPoint.getArgs();

		final boolean valid = constraintsEnforcer.validateMethodParameters(TARGET, SIGNATURE
				.getMethod(), parameterValues);

		return valid ? proceed() : null;
	}

	/**
	 * object validation before method execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): methodsPreValidateThis()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final boolean valid = constraintsEnforcer.validate(TARGET, false);

		return valid ? proceed() : null;
	}

	/**
	 * method return value validation
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): methodsWithReturnValue()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final Object returnValue = proceed();

		constraintsEnforcer.validateMethodReturnValue(TARGET, SIGNATURE.getMethod(), returnValue);

		return returnValue;
	}

	/**
	 * object validation after constructor execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	after() returning: constructorsPostValidateThis()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		constraintsEnforcer.validate(TARGET, true);
	}

	/**
	 * object validation after method execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	after() returning: methodsPostValidateThis()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		constraintsEnforcer.validate(TARGET, false);
	}

	/*
	 * GETTER
	 */

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
