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
public class MinLengthCheck extends AbstractAnnotationCheck<MinLength>
{
	private static final long serialVersionUID = 1L;

	private int min;

	@Override
	public void configure(final MinLength constraintAnnotation)
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

		final int len = value.toString().length();
		return len >= min;
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(final int min)
	{
		this.min = min;
	}
}
