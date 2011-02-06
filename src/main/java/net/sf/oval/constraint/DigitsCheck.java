/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class DigitsCheck extends AbstractAnnotationCheck<Digits>
{
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

		final BigDecimal value;

		if (valueToValidate instanceof BigDecimal)
			value = (BigDecimal) valueToValidate;
		else if (valueToValidate instanceof BigInteger)
			value = new BigDecimal((BigInteger) valueToValidate);
		else if (valueToValidate instanceof Integer)
			value = new BigDecimal((Integer) valueToValidate);
		else if (valueToValidate instanceof Long)
			value = new BigDecimal((Long) valueToValidate);
		else if (valueToValidate instanceof Short)
			value = new BigDecimal((Short) valueToValidate);
		else if (valueToValidate instanceof Byte)
			value = new BigDecimal((Byte) valueToValidate);
		else
			value = new BigDecimal(valueToValidate.toString());

		final int valueScale = value.scale();
		final int integerLen;
		if (BigDecimal.ZERO.compareTo(value) == 0)
			integerLen = 1;
		else
			integerLen = value.precision() - valueScale;
		final int fractionLen = valueScale > 0 ? valueScale : 0;

		return integerLen <= maxInteger && integerLen >= minInteger && fractionLen <= maxFraction
				&& fractionLen >= minFraction;
	}

	/**
	 * @param maxFraction the maxFraction to set
	 */
	public void setMaxFraction(final int maxFraction)
	{
		this.maxFraction = maxFraction;
	}

	/**
	 * @param maxInteger the maxInteger to set
	 */
	public void setMaxInteger(final int maxInteger)
	{
		this.maxInteger = maxInteger;
	}

	/**
	 * @param minFraction the minFraction to set
	 */
	public void setMinFraction(final int minFraction)
	{
		this.minFraction = minFraction;
	}

	/**
	 * @param minInteger the minInteger to set
	 */
	public void setMinInteger(final int minInteger)
	{
		this.minInteger = minInteger;
	}
}
