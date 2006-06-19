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
import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.PostValidateObject;
import net.sf.oval.annotations.PreValidateObject;

import org.aspectj.lang.annotation.SuppressAjWarnings;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public abstract aspect ConstraintsEnforcerAspect extends ApiUsageAuditor
{
	public ConstraintsEnforcerAspect()
	{
		// in case the this ConstraintsEnforcerAspect is used we can also use the ParameterNameResolver that utilizes the AspectJ library
		if (Validator.getParameterNameResolver() instanceof ParameterNameResolverDefaultImpl)
		{
			Validator.setParameterNameResolver(new ParameterNameResolverAspectJImpl());
		}
	}

	private final static Logger LOG = Logger.getLogger(ConstraintsEnforcerAspect.class.getName());

	/*
	 * POINT CUTS
	 */
	pointcut constructorsPostValidateObject(): execution(@PostValidateObject (@Constrained *).new(..));

	pointcut constructorsWithParameter(): execution((@Constrained *).new(*,..));

	pointcut methodsWithParameter() : execution(* (@Constrained *).*(*,..));

	pointcut methodsWithReturnValue() : execution(* (@Constrained *).*(..));

	pointcut methodsPostValidateObject(): execution(@PostValidateObject * (@Constrained *).*(..));

	pointcut methodsPreValidateObject(): execution(@PreValidateObject * (@Constrained *).*(..));

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

		ConstraintsEnforcer.validateConstructorParameters(TARGET, SIGNATURE.getConstructor(),
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

		final boolean valid = ConstraintsEnforcer.validateMethodParameters(TARGET, SIGNATURE
				.getMethod(), parameterValues);

		return valid ? proceed() : null;
	}

	/**
	 * object validation before method execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	Object around(): methodsPreValidateObject()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("around() " + SIGNATURE);

		final boolean valid = ConstraintsEnforcer.validate(TARGET, false);

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

		Object returnValue = proceed();

		ConstraintsEnforcer.validateMethodReturnValue(TARGET, SIGNATURE.getMethod(), returnValue);

		return returnValue;
	}

	/**
	 * object validation after constructor execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	after() returning: constructorsPostValidateObject()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final ConstructorSignature SIGNATURE = (ConstructorSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		ConstraintsEnforcer.validate(TARGET, true);
	}

	/**
	 * object validation after method execution
	 */
	@SuppressAjWarnings("adviceDidNotMatch")
	after() returning: methodsPostValidateObject()
	{
		final Object TARGET = thisJoinPoint.getTarget();
		final MethodSignature SIGNATURE = (MethodSignature) thisJoinPoint.getSignature();

		if (LOG.isLoggable(Level.FINE)) LOG.fine("after() " + SIGNATURE);

		ConstraintsEnforcer.validate(TARGET, false);
	}
}
