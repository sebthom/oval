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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.AbstractCheck;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.7 $
 */
public class SizeCheck extends AbstractCheck<Size>
{
	private int min;
	private int max;

	@Override
	public void configure(final Size constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMax(constraintAnnotation.max());
		setMin(constraintAnnotation.min());
	}

	/**
	 * @return the max
	 */
	public int getMax()
	{
		return max;
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{Integer.toString(min), Integer.toString(max)};
	}

	/**
	 * @return the min
	 */
	public int getMin()
	{
		return min;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value)
	{
		if (value == null) return true;

		if (value.getClass().isArray())
		{
			final int size = Array.getLength(value);
			return size >= min && size <= max;
		}
		if (value instanceof Collection)
		{
			final int size = ((Collection) value).size();
			return size >= min && size <= max;
		}
		if (value instanceof Map)
		{
			final int size = ((Map) value).size();
			return size >= min && size <= max;
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

	/**
	 * @param min the min to set
	 */
	public void setMin(final int min)
	{
		this.min = min;
	}
}
