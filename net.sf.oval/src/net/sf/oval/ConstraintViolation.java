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
package net.sf.oval;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;

import net.sf.oval.context.OValContext;

/**
 * An instance of this class provides detailed information about a single constraint violation 
 * that occured during validation.
 * 
 * @author Sebastian Thomschke
 */
public class ConstraintViolation implements Serializable
{
	private final static Logger LOG = Logger.getLogger(ConstraintViolation.class.getName());

	private final static long serialVersionUID = 1L;

	private final ConstraintViolation[] causes;
	private final OValContext context;
	private final String errorCode;
	private final String message;
	private final int priority;

	private transient Object validatedObject;
	private transient Object invalidValue;

	public ConstraintViolation(final String errorCode, final String message, final int priority,
			final Object validatedObject, final Object invalidValue, final OValContext context)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.priority = priority;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		causes = null;
	}

	public ConstraintViolation(final String errorCode, final String message, final int priority,
			final Object validatedObject, final Object invalidValue, final OValContext context,
			final ConstraintViolation... causes)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.priority = priority;
		this.validatedObject = validatedObject;
		this.invalidValue = invalidValue;
		this.context = context;
		this.causes = causes;
	}

	public ConstraintViolation(final String errorCode, final String message, final int priority,
			final Object validatedObject, final Object invalidValue, final OValContext context,
			final List<ConstraintViolation> causes)
	{
		this.errorCode = errorCode;
		this.message = message;
		this.priority = priority;
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
		return causes.clone();
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
	 * @return the priority
	 */
	public int getPriority()
	{
		return priority;
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
			ConstraintViolation.LOG
					.warning("Field 'validatedObject' not serialized because the field value object "
							+ validatedObject
							+ " of type "
							+ invalidValue.getClass()
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
			ConstraintViolation.LOG
					.warning("Field 'invalidValue' could not be serialized because the field value object "
							+ invalidValue + " does not implement java.io.Serializable.");
			// indicate value does not implement Serializable
			out.writeBoolean(false);
		}
	}
}
