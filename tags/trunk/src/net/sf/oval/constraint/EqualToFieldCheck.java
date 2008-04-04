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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.FieldNotFoundException;
import net.sf.oval.exception.InvokingMethodFailedException;
import net.sf.oval.exception.MethodNotFoundException;
import net.sf.oval.internal.CollectionFactoryHolder;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class EqualToFieldCheck extends AbstractAnnotationCheck<EqualToField>
{
	private static final long serialVersionUID = 1L;

	private boolean useGetter;

	private String fieldName;

	private Class< ? > declaringClass;

	@Override
	public void configure(final EqualToField constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setFieldName(constraintAnnotation.value());
		setDeclaringClass(constraintAnnotation.declaringClass());
		setUseGetter(constraintAnnotation.useGetter());
	}

	/**
	 * @return the declaringClass
	 */
	public Class< ? > getDeclaringClass()
	{
		return declaringClass;
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
		messageVariables.put("fieldName", fieldName);
		messageVariables.put("declaringClass", declaringClass == null
				|| declaringClass == Void.class ? null : declaringClass.getName());
		messageVariables.put("useGetter", Boolean.toString(useGetter));
		return messageVariables;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator)
	{
		if (valueToValidate == null) return true;

		final Class< ? > clazz = validatedObject.getClass();

		final Object valueToCompare;
		if (useGetter)
		{
			final Method getter = ReflectionUtils.getGetterRecursive(clazz, fieldName);
			if (getter == null)
				throw new MethodNotFoundException("Getter for field <" + fieldName
						+ "> not found in class <" + clazz + "> or it's super classes.");

			try
			{
				valueToCompare = getter.invoke(validatedObject);
			}
			catch (final Exception ex)
			{
				throw new InvokingMethodFailedException(getter.getName(), validatedObject,
						new MethodReturnValueContext(getter), ex);
			}
		}
		else
		{
			final Field field = ReflectionUtils.getFieldRecursive(clazz, fieldName);

			if (field == null)
				throw new FieldNotFoundException("Field <" + fieldName + "> not found in class <"
						+ clazz + "> or it's super classes.");

			valueToCompare = ReflectionUtils.getFieldValue(field, validatedObject);
		}

		if (valueToCompare == null) return false;

		return valueToValidate.equals(valueToCompare);
	}

	/**
	 * @return the useGetter
	 */
	public boolean isUseGetter()
	{
		return useGetter;
	}

	/**
	 * @param declaringClass the declaringClass to set
	 */
	public void setDeclaringClass(final Class< ? > declaringClass)
	{
		this.declaringClass = declaringClass == Void.class ? null : declaringClass;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(final String fieldName)
	{
		this.fieldName = fieldName;
	}

	/**
	 * @param useGetter the useGetter to set
	 */
	public void setUseGetter(final boolean useGetter)
	{
		this.useGetter = useGetter;
	}
}
