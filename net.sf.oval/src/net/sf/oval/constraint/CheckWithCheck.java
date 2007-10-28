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
package net.sf.oval.constraint;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class CheckWithCheck extends AbstractAnnotationCheck<CheckWith>
{
	public interface SimpleCheck extends Serializable
	{
		boolean isSatisfied(Object validatedObject, Object value);
	}

	private static final long serialVersionUID = 1L;

	private boolean ignoreIfNull;
	private SimpleCheck simpleCheck;

	@Override
	public void configure(final CheckWith constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setSimpleCheck(constraintAnnotation.value());
		setIgnoreIfNull(constraintAnnotation.ignoreIfNull());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(4);
		messageVariables.put("ignoreIfNull", Boolean.toString(ignoreIfNull));
		messageVariables.put("simpleCheck", simpleCheck.getClass().getName());
		return messageVariables;
	}

	/**
	 * @return the simpleCheck
	 */
	public SimpleCheck getSimpleCheck()
	{
		return simpleCheck;
	}

	/**
	 * @return the ignoreIfNull
	 */
	public boolean isIgnoreIfNull()
	{
		return ignoreIfNull;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator) throws ReflectionException
	{
		if (value == null && ignoreIfNull) return true;

		return simpleCheck.isSatisfied(validatedObject, value);
	}

	/**
	 * @param ignoreIfNull the ignoreIfNull to set
	 */
	public void setIgnoreIfNull(final boolean ignoreIfNull)
	{
		this.ignoreIfNull = ignoreIfNull;
	}

	/**
	 * @param simpleCheckType the simpleCheckType to set
	 */
	public void setSimpleCheck(final Class< ? extends SimpleCheck> simpleCheckType)
			throws ReflectionException, IllegalArgumentException
	{
		if (simpleCheckType == null)
			throw new IllegalArgumentException("simpleCheckType cannot be null");

		try
		{
			final Constructor< ? extends SimpleCheck> ctor = simpleCheckType
					.getDeclaredConstructor((Class< ? >[]) null);
			ctor.setAccessible(true);
			simpleCheck = ctor.newInstance();
		}
		catch (final Exception ex)
		{
			throw new ReflectionException("Cannot instantiate an object of type  "
					+ simpleCheckType.getName(), ex);
		}
	}

	/**
	 * @param simpleCheck the simpleCheck to set
	 */
	public void setSimpleCheck(final SimpleCheck simpleCheck) throws IllegalArgumentException
	{
		if (simpleCheck == null) throw new IllegalArgumentException("simpleCheck cannot be null");

		this.simpleCheck = simpleCheck;
	}
}
