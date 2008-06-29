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

import static net.sf.oval.Validator.getCollectionFactory;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MaxSizeCheck extends AbstractAnnotationCheck<MaxSize>
{
	private static final long serialVersionUID = 1L;

	private int max;

	@Override
	public void configure(final MaxSize constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMax(constraintAnnotation.value());
	}

	/**
	 * @return the max
	 */
	public int getMax()
	{
		return max;
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
		messageVariables.put("max", Integer.toString(max));
		return messageVariables;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		if (valueToValidate instanceof Collection)
		{
			final int size = ((Collection< ? >) valueToValidate).size();
			return size <= max;
		}
		if (valueToValidate instanceof Map)
		{
			final int size = ((Map< ? , ? >) valueToValidate).size();
			return size <= max;
		}
		if (valueToValidate.getClass().isArray())
		{
			final int size = Array.getLength(valueToValidate);
			return size <= max;
		}
		return false;
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(final int max)
	{
		this.max = max;
	}
}
