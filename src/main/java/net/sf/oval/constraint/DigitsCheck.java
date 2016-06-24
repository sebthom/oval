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

import static net.sf.oval.Validator.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public class DigitsCheck extends AbstractAnnotationCheck<Digits>
{
	private static final Log LOG = Log.getLog(DigitsCheck.class);

	private static final long serialVersionUID = 1L;

	private int maxFraction = Integer.MAX_VALUE;
	private int maxInteger = Integer.MAX_VALUE;
	private int minFraction = 0;
	private int minInteger = 0;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final Digits constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMinInteger(constraintAnnotation.minInteger());
		setMaxInteger(constraintAnnotation.maxInteger());
		setMinFraction(constraintAnnotation.minFraction());
		setMaxFraction(constraintAnnotation.maxFraction());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Map<String, String> createMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
		messageVariables.put("maxInteger", Integer.toString(maxInteger));
		messageVariables.put("minInteger", Integer.toString(minInteger));
		messageVariables.put("maxFraction", Integer.toString(maxFraction));
		messageVariables.put("minFraction", Integer.toString(minFraction));
		return messageVariables;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected ConstraintTarget[] getAppliesToDefault()
	{
		return new ConstraintTarget[]{ConstraintTarget.VALUES};
	}

	/**
	 * @return the maxFraction
	 */
	public int getMaxFraction()
	{
		return maxFraction;
	}

	/**
	 * @return the maxInteger
	 */
	public int getMaxInteger()
	{
		return maxInteger;
	}

	/**
	 * @return the minFraction
	 */
	public int getMinFraction()
	{
		return minFraction;
	}

	/**
	 * @return the minInteger
	 */
	public int getMinInteger()
	{
		return minInteger;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		final int fractLen, intLen;
		if (valueToValidate instanceof Integer)
		{
			final int value = (Integer) valueToValidate;
			intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
			fractLen = 0;
		}
		else if (valueToValidate instanceof Long)
		{
			final long value = (Long) valueToValidate;
			intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
			fractLen = 0;
		}
		else if (valueToValidate instanceof Short)
		{
			final short value = (Short) valueToValidate;
			intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
			fractLen = 0;
		}
		else if (valueToValidate instanceof Byte)
		{
			final byte value = (Byte) valueToValidate;
			intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
			fractLen = 0;
		}
		else if (valueToValidate instanceof BigInteger)
		{
			final long value = ((BigInteger) valueToValidate).longValue();
			intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
			fractLen = 0;
		}
		else
		{
			BigDecimal value = null;
			if (valueToValidate instanceof BigDecimal)
			{
				value = (BigDecimal) valueToValidate;
			}
			else
			{
				try
				{
					value = new BigDecimal(valueToValidate.toString());
				}
				catch (final NumberFormatException ex)
				{
					LOG.debug("Failed to parse numeric value: " + valueToValidate, ex);
					return false;
				}
			}
			final int valueScale = value.scale();
			final long longValue = value.longValue();
			intLen = longValue == 0 ? 1 : (int) Math.log10(longValue) + 1;
			fractLen = valueScale > 0 ? valueScale : 0;
		}

		return intLen <= maxInteger && intLen >= minInteger && fractLen <= maxFraction && fractLen >= minFraction;
	}

	/**
	 * @param maxFraction the maxFraction to set
	 */
	public void setMaxFraction(final int maxFraction)
	{
		this.maxFraction = maxFraction;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param maxInteger the maxInteger to set
	 */
	public void setMaxInteger(final int maxInteger)
	{
		this.maxInteger = maxInteger;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param minFraction the minFraction to set
	 */
	public void setMinFraction(final int minFraction)
	{
		this.minFraction = minFraction;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param minInteger the minInteger to set
	 */
	public void setMinInteger(final int minInteger)
	{
		this.minInteger = minInteger;
		requireMessageVariablesRecreation();
	}
}
