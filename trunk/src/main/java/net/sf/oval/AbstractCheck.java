/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval;

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Collections;
import java.util.Map;

import net.sf.oval.context.OValContext;
import net.sf.oval.expression.ExpressionLanguage;

/**
 * Partial implementation of check classes.
 * 
 * @author Sebastian Thomschke
 */
public abstract class AbstractCheck implements Check
{
	private static final long serialVersionUID = 1L;

	private OValContext context;
	private String errorCode;
	private String message;
	private Map<String, String> messageVariables;
	private Map<String, String> messageVariablesUnmodifiable;
	private boolean messageVariablesUpToDate = true;

	private String[] profiles;
	private int severity;
	private String when;
	private String whenFormula;
	private String whenLang;

	protected Map<String, String> createMessageVariables()
	{
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	public OValContext getContext()
	{
		return context;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getErrorCode()
	{
		/*
		 * if the error code has not been initialized (which might be the case when using XML configuration),
		 * construct the string based on this class' name minus the appendix "Check"
		 */
		if (errorCode == null)
		{
			final String className = getClass().getName();
			if (className.endsWith("Check"))
			{
				errorCode = className.substring(0, getClass().getName().length() - "Check".length());
			}
			else
			{
				errorCode = className;
			}
		}
		return errorCode;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getMessage()
	{
		/*
		 * if the message has not been initialized (which might be the case when using XML configuration),
		 * construct the string based on this class' name minus the appendix "Check" plus the appendix ".violated"
		 */
		if (message == null)
		{
			final String className = getClass().getName();
			if (className.endsWith("Check"))
			{
				message = className.substring(0, getClass().getName().length() - "Check".length()) + ".violated";
			}
			else
			{
				message = className + ".violated";
			}
		}
		return message;
	}

	/**
	 * Values that are used to fill place holders when rendering the error message.
	 * A key "min" with a value "4" will replace the place holder {min} in an error message
	 * like "Value cannot be smaller than {min}" with the string "4".
	 * 
	 * <b>Note:</b> Override {@link #createMessageVariables()} to create and fill the map
	 * 
	 * @return an unmodifiable map
	 */
	public final Map<String, String> getMessageVariables()
	{
		if (!messageVariablesUpToDate)
		{
			messageVariables = createMessageVariables();
			if (messageVariables == null)
			{
				messageVariablesUnmodifiable = null;
			}
			else
			{
				messageVariablesUnmodifiable = Collections.unmodifiableMap(messageVariables);
			}
			messageVariablesUpToDate = true;
		}
		return messageVariablesUnmodifiable;
	}

	/**
	 * {@inheritDoc}
	 */
	public String[] getProfiles()
	{
		return profiles;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getSeverity()
	{
		return severity;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getWhen()
	{
		return when == null ? null : whenLang + ":" + when;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isActive(final Object validatedObject, final Object valueToValidate, final Validator validator)
	{
		if (when == null) return true;

		final Map<String, Object> values = getCollectionFactory().createMap();
		values.put("_value", valueToValidate);
		values.put("_this", validatedObject);

		final ExpressionLanguage el = validator.getExpressionLanguage(whenLang);
		return el.evaluateAsBoolean(whenFormula, values);
	}

	/**
	 * Calling this method indicates that the {@link #createMessageVariables()} method needs to be called before the message 
	 * for the next violation of this check is rendered.
	 */
	protected void requireMessageVariablesRecreation()
	{
		messageVariablesUpToDate = false;
	}
	
	/**
	 * {@inheritDoc}
	 */
	public void setContext(OValContext context)
	{
		this.context = context;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setErrorCode(final String failureCode)
	{
		errorCode = failureCode;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProfiles(final String... profiles)
	{
		this.profiles = profiles;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setSeverity(final int severity)
	{
		this.severity = severity;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setWhen(final String when)
	{
		if (when == null || when.length() == 0)
		{
			this.when = null;
			this.whenFormula = null;
			this.whenLang = null;
		}
		else
		{
			this.when = when;
			final String[] parts = when.split(":", 2);
			if (parts.length == 0)
				throw new IllegalArgumentException("[when] is missing the scripting language declaration");
			whenLang = parts[0];
			whenFormula = parts[1];
		}
	}
}
