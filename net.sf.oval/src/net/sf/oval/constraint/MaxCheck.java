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

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.collection.CollectionFactoryHolder;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MaxCheck extends AbstractAnnotationCheck<Max>
{
	private static final long serialVersionUID = 1L;

	private long max;

	@Override
	public void configure(final Max constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMax(constraintAnnotation.value());
	}

	/**
	 * @return the max
	 */
	public long getMax()
	{
		return max;
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
		messageVariables.put("max", Long.toString(max));
		return messageVariables;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator)
	{
		if (value == null) return true;

		if (value instanceof Number)
		{
			if (value instanceof Float || value instanceof Double)
			{
				final double doubleValue = ((Number) value).doubleValue();
				return doubleValue <= max;
			}
			final long longValue = ((Number) value).longValue();
			return longValue <= max;
		}

		final String stringValue = value.toString();
		try
		{
			final double doubleValue = Double.parseDouble(stringValue);
			return doubleValue <= max;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(final long max)
	{
		this.max = max;
	}
}
