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
package net.sf.oval;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * An instance of this class provides detailed information about a single constraint 
 * violation that occurred during validation.
 * 
 * @author Sebastian Thomschke
 */
public class ConstraintViolation implements Serializable
{
	private static final Log LOG = Log.getLog(ConstraintViolation.class);

	private static final long serialVersionUID = 1L;

	private final String checkName;
	private final ConstraintViolation[] causes;
	private final OValContext context;
	private final String errorCode;
	private final String message;
	private final String messageTemplate;
	private final Map<String, String> messageVariables;
	private final int severity;

	private transient Object validatedObject;
	private transient Object invalidValue;

	public ConstraintViolation(final Check check, final String message, final Object validatedObject,
			final Object invalidValue, final OValContext context)
	{
		this(check, message, validatedObject, invalidValue, context, (ConstraintViolation[]) null);
	}

	public ConstraintViolation(final Check check, final String message, final Object validatedObject,
			final Object invalidValue, final OValContext context, final ConstraintViolation... causes)
	{
		this.checkName = check.getClass().getName();
		this.errorCode = check.getErrorCode();
		this.message = message;
		this.messageTemplate = check.getMessage();
		this.messageVariables = check.getMessageVariables();
		this.severity = check.getSeverity();
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		this.causes = causes != null && causes.length == 0 ? null : causes;
	}

	public ConstraintViolation(final Check check, final String message, final Object validatedObject,
			final Object invalidValue, final OValContext context, final List<ConstraintViolation> causes)
	{
		this.checkName = check.getClass().getName();
		this.errorCode = check.getErrorCode();
		this.message = message;
		this.messageTemplate = check.getMessage();
		this.messageVariables = check.getMessageVariables();
		this.severity = check.getSeverity();
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		this.causes = causes != null && causes.size() == 0 ? null : causes.toArray(new ConstraintViolation[causes
				.size()]);
	}

	/**
	 * @return the causes or null of no causes exists
	 */
	public ConstraintViolation[] getCauses()
	{
		return causes == null ? null : causes.clone();
	}

	/**
	 * @return the fully qualified class name of the corresponding check
	 */
	public String getCheckName()
	{
		return checkName;
	}

	/**
	 * @return Returns the context where the constraint violation occurred.
	 * 
	 * @see net.sf.oval.context.ClassContext
	 * @see net.sf.oval.context.FieldContext
	 * @see net.sf.oval.context.MethodEntryContext
	 * @see net.sf.oval.context.MethodExitContext
	 * @see net.sf.oval.context.MethodParameterContext
	 * @see net.sf.oval.context.MethodReturnValueContext
	 */
	public OValContext getContext()
	{
		return context;
	}

	/**
	 * @return the error code
	 */
	public String getErrorCode()
	{
		return errorCode;
	}

	/**
	 * @return Returns the value that was validated.
	 */
	public Object getInvalidValue()
	{
		return invalidValue;
	}

	/**
	 * @return the localized and rendered message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @return the raw message specified for the constraint without variable resolution and localization
	 */
	public String getMessageTemplate()
	{
		return messageTemplate;
	}

	/**
	 * Returns the message variables provided by the corresponding check.
	 * @return an unmodifiable map holding the message variables provided by the corresponding check.
	 */
	public Map<String, String> getMessageVariables()
	{
		return messageVariables;
	}

	/**
	 * @return the severity
	 */
	public int getSeverity()
	{
		return severity;
	}

	/**
	 * @return the validatedObject
	 */
	public Object getValidatedObject()
	{
		return validatedObject;
	}

	/**
	 * see http://java.sun.com/developer/technicalArticles/ALT/serialization/
	 * 
	 * @param in
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		if (in.readBoolean())
		{
			validatedObject = in.readObject();
		}
		if (in.readBoolean())
		{
			invalidValue = in.readObject();
		}
	}

	/**
	 * {@inheritDoc}
	 */

	@Override
	public String toString()
	{
		return getClass().getName() + ": " + message;
	}

	/**
	 * see http://java.sun.com/developer/technicalArticles/ALT/serialization/
	 * 
	 * @param out
	 * @throws IOException
	 */
	private void writeObject(final java.io.ObjectOutputStream out) throws IOException
	{
		out.defaultWriteObject();
		if (validatedObject instanceof Serializable)
		{
			// indicate validatedObject implements Serializable
			out.writeBoolean(true);
			out.writeObject(validatedObject);
		}
		else
		{
			LOG.warn("Field 'validatedObject' not serialized because the field value object " + validatedObject
					+ " of type " + invalidValue.getClass() + " does not implement " + Serializable.class.getName());

			// indicate validatedObject does not implement Serializable
			out.writeBoolean(false);
		}

		if (invalidValue instanceof Serializable)
		{
			// indicate value implements Serializable
			out.writeBoolean(true);
			out.writeObject(invalidValue);
		}
		else
		{
			final String warning = //
			"Field 'invalidValue' could not be serialized because the field value object {1} does not implement java.io.Serializable.";
			LOG.warn(warning, invalidValue);
			// indicate value does not implement Serializable
			out.writeBoolean(false);
		}
	}
}
