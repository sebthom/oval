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

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MinCheck extends AbstractAnnotationCheck<Min>
{
	private static final long serialVersionUID = 1L;

	private double min;

	@Override
	public void configure(final Min constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMin(constraintAnnotation.value());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
		messageVariables.put("min", Double.toString(min));
		return messageVariables;
	}

	/**
	 * @return the min
	 */
	public double getMin()
	{
		return min;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		if (valueToValidate instanceof Number)
		{
			final double doubleValue = ((Number) valueToValidate).doubleValue();
			return doubleValue >= min;
		}

		final String stringValue = valueToValidate.toString();
		try
		{
			final double doubleValue = Double.parseDouble(stringValue);
			return doubleValue >= min;
		}
		catch (final NumberFormatException e)
		{
			return false;
		}
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(final double min)
	{
		this.min = min;
	}
}
