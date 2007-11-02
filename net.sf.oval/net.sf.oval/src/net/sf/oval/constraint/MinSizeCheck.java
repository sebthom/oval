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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.CollectionFactoryHolder;

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
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
		messageVariables.put("min", Integer.toString(min));
		return messageVariables;
	}

	/**
	 * @return the min
	 */
	public int getMin()
	{
		return min;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator)
	{
		if (value == null) return true;

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
		if (value.getClass().isArray())
		{
			final int size = Array.getLength(value);
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
