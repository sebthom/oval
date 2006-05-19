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
package net.sf.oval.constraints;

import java.lang.reflect.Method;

import net.sf.oval.AbstractCheck;
import net.sf.oval.exceptions.ReflectionException;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.7 $
 */
public class ValidateWithMethodCheck extends AbstractCheck<ValidateWithMethod>
{
	private String methodName;
	private Class parameterType;

	@Override
	public void configure(final ValidateWithMethod constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMethodName(constraintAnnotation.methodName());
		setParameterType(constraintAnnotation.parameterType());
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

	public boolean isSatisfied(final Object validatedObject, final Object value)
	{
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
