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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
 */
public class DateRangeCheck extends AbstractAnnotationCheck<DateRange>
{
	private final static Logger LOG = Logger.getLogger(DateRangeCheck.class.getName());

	private static final long serialVersionUID = 1L;

	private String format;
	private String max;
	private String min;

	private transient Long maxMillis;
	private transient Long minMillis;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final DateRange constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setMin(constraintAnnotation.min());
		setMax(constraintAnnotation.max());
		setFormat(constraintAnnotation.format());
	}

	/**
	 * @return the format
	 */
	public String getFormat()
	{
		return format;
	}

	/**
	 * @return the max
	 */
	public String getMax()
	{
		return max;
	}

	private long getMaxMillis() throws InvalidConfigurationException
	{
		if (maxMillis == null)
		{
			if (max == null || max.length() == 0)
				return Long.MAX_VALUE;
			else if ("now".equals(max))
				return System.currentTimeMillis();
			else if ("today".equals(max))
			{
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DAY_OF_YEAR, 1);
				cal.add(Calendar.MILLISECOND, -1);
				return cal.getTimeInMillis();
			}
			else if ("tomorrow".equals(max))
			{
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DAY_OF_YEAR, 2);
				cal.add(Calendar.MILLISECOND, -1);
				return cal.getTimeInMillis();
			}
			else if ("yesterday".equals(max))
			{
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.MILLISECOND, -1);
				return cal.getTimeInMillis();
			}
			else if (format != null && format.length() > 0)
			{
				final SimpleDateFormat sdf = new SimpleDateFormat(format);
				try
				{
					maxMillis = sdf.parse(max).getTime();
				}
				catch (final ParseException e)
				{
					throw new InvalidConfigurationException("Unable to parse the max Date String", e);
				}
			}
			else
			{
				try
				{
					maxMillis = DateFormat.getDateTimeInstance().parse(max).getTime();
				}
				catch (final ParseException e)
				{
					throw new InvalidConfigurationException("Unable to parse the max Date String", e);
				}
			}
		}
		return maxMillis;
	}

	/**
	 * @return the min
	 */
	public String getMin()
	{
		return min;
	}

	private long getMinMillis() throws InvalidConfigurationException
	{
		if (minMillis == null)
		{
			if (min == null || min.length() == 0)
				return 0L;
			else if ("now".equals(min))
				return System.currentTimeMillis();
			else if ("today".equals(max))
			{
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				return cal.getTimeInMillis();
			}
			else if ("tomorrow".equals(max))
			{
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DAY_OF_YEAR, 1);
				return cal.getTimeInMillis();
			}
			else if ("yesterday".equals(max))
			{
				final Calendar cal = Calendar.getInstance();
				cal.set(Calendar.HOUR_OF_DAY, 0);
				cal.set(Calendar.MINUTE, 0);
				cal.set(Calendar.SECOND, 0);
				cal.set(Calendar.MILLISECOND, 0);
				cal.add(Calendar.DAY_OF_YEAR, -1);
				return cal.getTimeInMillis();
			}
			else if (format != null && format.length() > 0)
			{
				final SimpleDateFormat sdf = new SimpleDateFormat(format);
				try
				{
					minMillis = sdf.parse(min).getTime();
				}
				catch (final ParseException e)
				{
					throw new InvalidConfigurationException("Unable to parse the min Date String", e);
				}
			}
			else
			{
				try
				{
					minMillis = DateFormat.getDateTimeInstance().parse(min).getTime();
				}
				catch (final ParseException e)
				{
					throw new InvalidConfigurationException("Unable to parse the min Date String", e);
				}
			}
		}
		return minMillis;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		long valueInMillis = -1;

		// check if the value is a Date
		if (valueToValidate instanceof Date)
		{
			valueInMillis = ((Date) valueToValidate).getTime();
		}

		// check if the value is a Calendar
		else if (valueToValidate instanceof Calendar)
		{
			valueInMillis = ((Calendar) valueToValidate).getTime().getTime();
		}

		else
		{
			// see if we can extract a date based on the object's String representation
			final String stringValue = valueToValidate.toString();
			try
			{
				if (format != null)
				{
					try
					{
						valueInMillis = new SimpleDateFormat(format).parse(stringValue).getTime();
					}
					catch (final ParseException ex)
					{
						LOG.log(Level.FINE, "valueToValidate not parsable with specified format " + format, ex);
					}
				}

				if (valueInMillis == -1)
				{
					valueInMillis = DateFormat.getDateTimeInstance().parse(stringValue).getTime();
				}
			}
			catch (final ParseException ex)
			{
				LOG.log(Level.FINE, "valueToValidate is unparsable.", ex);
				return false;
			}
		}

		return valueInMillis >= getMinMillis() && valueInMillis <= getMaxMillis();
	}

	/**
	 * @param format the format to set
	 */
	public void setFormat(final String format)
	{
		this.format = format;
		requirekMessageVariablesUpdate();
	}

	/**
	 * @param max the max to set
	 */
	public void setMax(final String max)
	{
		this.max = max;
		maxMillis = null;
		requirekMessageVariablesUpdate();
	}

	/**
	 * @param min the min to set
	 */
	public void setMin(final String min)
	{
		this.min = min;
		minMillis = null;
		requirekMessageVariablesUpdate();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> updateMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(3);
		messageVariables.put("min", min == null ? ".." : min);
		messageVariables.put("max", max == null ? ".." : max);
		messageVariables.put("format", format);
		return messageVariables;
	}
}
