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

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MinCheck extends AbstractAnnotationCheck<Min>
{
	private static final long serialVersionUID = 1L;

	private long min;

	@Override
	public void configure(final Min constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMin(constraintAnnotation.value());
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{Long.toString(min)};
	}

	/**
	 * @return the min
	 */
	public long getMin()
	{
		return min;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		if (value instanceof Number)
		{
			if (value instanceof Float || value instanceof Double)
			{
				final double doubleValue = ((Number) value).doubleValue();
				return doubleValue >= min;
			}
			final long longValue = ((Number) value).longValue();
			return longValue >= min;
		}

		final String stringValue = value.toString();
		try
		{
			final double doubleValue = Double.parseDouble(stringValue);
			return doubleValue >= min;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(final long min)
	{
		this.min = min;
	}
}
