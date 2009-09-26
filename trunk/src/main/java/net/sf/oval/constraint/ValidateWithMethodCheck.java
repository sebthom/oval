/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval.constraint;

import static net.sf.oval.Validator.getCollectionFactory;

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class ValidateWithMethodCheck extends AbstractAnnotationCheck<ValidateWithMethod>
{
	private static final long serialVersionUID = 1L;

	private boolean ignoreIfNull;
	private String methodName;
	private Class< ? > parameterType;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final ValidateWithMethod constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMethodName(constraintAnnotation.methodName());
		setParameterType(constraintAnnotation.parameterType());
		setIgnoreIfNull(constraintAnnotation.ignoreIfNull());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> createMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(4);
		messageVariables.put("ignoreIfNull", Boolean.toString(ignoreIfNull));
		messageVariables.put("methodName", methodName);
		messageVariables.put("parameterType", parameterType.getName());
		return messageVariables;
	}

	/**
	 * {@inheritDoc}
	 */
	protected ConstraintTarget[] getAppliesToDefault()
	{
		return new ConstraintTarget[]{ConstraintTarget.VALUES};
	}
	
	/**
	 * @return the methodName
	 */
	public String getMethodName()
	{
		return methodName;
	}

	/**
	 * @return the parameterType
	 */
	public Class< ? > getParameterType()
	{
		return parameterType;
	}

	/**
	 * @return the ignoreIfNull
	 */
	public boolean isIgnoreIfNull()
	{
		return ignoreIfNull;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator) throws ReflectionException
	{
		if (valueToValidate == null && ignoreIfNull) return true;

		final Method method = ReflectionUtils.getMethodRecursive(validatedObject.getClass(), methodName, parameterType);
		return ((Boolean) ReflectionUtils.invokeMethod(method, validatedObject, valueToValidate)).booleanValue();
	}

	/**
	 * @param ignoreIfNull the ignoreIfNull to set
	 */
	public void setIgnoreIfNull(final boolean ignoreIfNull)
	{
		this.ignoreIfNull = ignoreIfNull;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(final String methodName)
	{
		this.methodName = methodName;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param parameterType the parameterType to set
	 */
	public void setParameterType(final Class< ? > parameterType)
	{
		this.parameterType = parameterType;
		requireMessageVariablesRecreation();
	}
}
