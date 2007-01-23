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
package net.sf.oval.constraints;

import java.lang.reflect.Method;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.ReflectionException;

/**
 * @author Sebastian Thomschke
 */
public class ValidateWithMethodCheck extends AbstractAnnotationCheck<ValidateWithMethod>
{
	private static final long serialVersionUID = 1L;
	
	private boolean ignoreIfNull;
	private String methodName;
	private Class parameterType;

	@Override
	public void configure(final ValidateWithMethod constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMethodName(constraintAnnotation.methodName());
		setParameterType(constraintAnnotation.parameterType());
		setIgnoreIfNull(constraintAnnotation.ignoreIfNull());
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{methodName, parameterType.getName()};
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
	public Class getParameterType()
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

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context) throws ReflectionException
	{
		if (value == null && ignoreIfNull) return true;
		
		try
		{
			final Method method = validatedObject.getClass().getDeclaredMethod(methodName,
					parameterType);
			method.setAccessible(true);
			return ((Boolean) method.invoke(validatedObject, value)).booleanValue();
		}
		catch (Exception e)
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
	public void setParameterType(final Class parameterType)
	{
		this.parameterType = parameterType;
	}
}
