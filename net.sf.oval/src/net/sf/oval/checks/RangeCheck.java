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
package net.sf.oval.checks;

import java.util.Map;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.constraints.Range;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class RangeCheck extends AbstractAnnotationCheck<Range>
{
	private static final long serialVersionUID = 1L;

	private long min = Long.MIN_VALUE;
	private long max = Long.MAX_VALUE;

	@Override
	public void configure(final Range constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMax(constraintAnnotation.max());
		setMin(constraintAnnotation.min());
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
		final Map<String, String> messageVariables = CollectionFactory.INSTANCE.createMap(2);
		messageVariables.put("max", Long.toString(max));
		messageVariables.put("min", Long.toString(min));
		return messageVariables;
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
				return doubleValue >= min && doubleValue <= max;
			}
			final long longValue = ((Number) value).longValue();
			return longValue >= min && longValue <= max;
		}

		final String stringValue = value.toString();
		try
		{
			final double doubleValue = Double.parseDouble(stringValue);
			return doubleValue >= min && doubleValue <= max;
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

	/**
	 * @param min the min to set
	 */
	public void setMin(final long min)
	{
		this.min = min;
	}
}
