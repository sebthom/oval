/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import java.math.BigDecimal;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class NotNegativeCheck extends AbstractAnnotationCheck<NotNegative>
{
	private static final BigDecimal ZERO = BigDecimal.valueOf(0);

	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ConstraintTarget[] getAppliesToDefault()
	{
		return new ConstraintTarget[]{ConstraintTarget.VALUES};
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		if (valueToValidate instanceof Number)
		{
			if (valueToValidate instanceof Float || valueToValidate instanceof Double)
				return ((Number) valueToValidate).doubleValue() >= 0;
			if (valueToValidate instanceof BigDecimal) return ((BigDecimal) valueToValidate).compareTo(ZERO) >= 0;
			return ((Number) valueToValidate).longValue() >= 0;
		}

		final String stringValue = valueToValidate.toString();
		try
		{
			return Double.parseDouble(stringValue) >= 0;
		}
		catch (final NumberFormatException e)
		{
			return false;
		}
	}
}
