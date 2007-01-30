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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.constraints.MinSize;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MinSizeCheck extends AbstractAnnotationCheck<MinSize>
{
	private static final long serialVersionUID = 1L;

	private int min;

	@Override
	public void configure(final MinSize constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMin(constraintAnnotation.value());
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{Integer.toString(min)};
	}

	/**
	 * @return the min
	 */
	public int getMin()
	{
		return min;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		if (value.getClass().isArray())
		{
			final int size = Array.getLength(value);
			return size >= min;
		}
		if (value instanceof Collection)
		{
			final int size = ((Collection) value).size();
			return size >= min;
		}
		if (value instanceof Map)
		{
			final int size = ((Map) value).size();
			return size >= min;
		}
		return false;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(final int min)
	{
		this.min = min;
	}
}
