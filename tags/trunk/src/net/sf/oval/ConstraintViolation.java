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

import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * An instance of this class provides detailed information about a single constraint violation 
 * that occured during validation.
 * 
 * @author Sebastian Thomschke
 */
public class ConstraintViolation implements Serializable
{
	private final static Log LOG = Log.getLog(ConstraintViolation.class);

	private final static long serialVersionUID = 1L;

	private final ConstraintViolation[] causes;
	private final OValContext context;
	private final String errorCode;
	private final String message;
	private final int severity;

	private transient Object validatedObject;
	private transient Object invalidValue;

	public ConstraintViolation(final String errorCode, final String message, final int severity,
			final Object validatedObject, final Object invalidValue, final OValContext context)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.severity = severity;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		causes = null;
	}

	public ConstraintViolation(final String errorCode, final String message, final int severity,
			final Object validatedObject, final Object invalidValue, final OValContext context,
			final ConstraintViolation... causes)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.severity = severity;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		this.causes = causes;
	}

	public ConstraintViolation(final String errorCode, final String message, final int severity,
			final Object validatedObject, final Object invalidValue, final OValContext context,
			final List<ConstraintViolation> causes)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.severity = severity;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		this.causes = causes.toArray(new ConstraintViolation[causes.size()]);
	}

	/**
	 * @return the causes
	 */
	public ConstraintViolation[] getCauses()
	{
		return causes == null ? null : causes.clone();
	}

	/**
	 * @return Returns the context where the constraint violation occured.
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
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
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
	private void readObject(final java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
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
	private synchronized void writeObject(final java.io.ObjectOutputStream out) throws IOException
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
			LOG.warn("Field 'validatedObject' not serialized because the field value object "
					+ validatedObject + " of type " + invalidValue.getClass()
					+ " does not implement " + Serializable.class.getName());

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
			LOG
					.warn(
							"Field 'invalidValue' could not be serialized because the field value object {} does not implement java.io.Serializable.",
							invalidValue);
			// indicate value does not implement Serializable
			out.writeBoolean(false);
		}
	}
}
