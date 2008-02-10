/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

import java.lang.reflect.Method;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class ValidateWithMethodCheck extends AbstractAnnotationCheck<ValidateWithMethod>
{
	private static final long serialVersionUID = 1L;

	private boolean ignoreIfNull;
	private String methodName;
	private Class< ? > parameterType;

	@Override
	public void configure(final ValidateWithMethod constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMethodName(constraintAnnotation.methodName());
		setParameterType(constraintAnnotation.parameterType());
		setIgnoreIfNull(constraintAnnotation.ignoreIfNull());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(4);
		messageVariables.put("ignoreIfNull", Boolean.toString(ignoreIfNull));
		messageVariables.put("methodName", methodName);
		messageVariables.put("parameterType", parameterType.getName());
		return messageVariables;
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

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator) throws ReflectionException
	{
		if (valueToValidate == null && ignoreIfNull) return true;

		try
		{
			final Method method = validatedObject.getClass().getDeclaredMethod(methodName,
					parameterType);
			method.setAccessible(true);
			return ((Boolean) method.invoke(validatedObject, valueToValidate)).booleanValue();
		}
		catch (final Exception e)
		{
			throw new ReflectionException("Calling validation method "
					+ validatedObject.getClass().getName() + "." + methodName + "("
					+ validatedObject.getClass().getName() + ") failed.", e);
		}
	}

	/**
	 * @param ignoreIfNull the ignoreIfNull to set
	 */
	public void setIgnoreIfNull(final boolean ignoreIfNull)
	{
		this.ignoreIfNull = ignoreIfNull;
	}

	/**
	 * @param methodName the methodName to set
	 */
	public void setMethodName(final String methodName)
	{
		this.methodName = methodName;
	}

	/**
	 * @param parameterType the parameterType to set
	 */
	public void setParameterType(final Class< ? > parameterType)
	{
		this.parameterType = parameterType;
	}
}
