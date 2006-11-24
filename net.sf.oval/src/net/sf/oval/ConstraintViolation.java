/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
import java.util.logging.Logger;

import net.sf.oval.contexts.OValContext;

/**
 * An instance of this class provides detailed information about a single constraint violation 
 * that was detected during validation.
 * 
 * @author Sebastian Thomschke
 */
public class ConstraintViolation implements Serializable
{
	private final static Logger LOG = Logger.getLogger(ConstraintViolation.class.getName());

	private final static long serialVersionUID = 1L;

	private final ConstraintViolation[] causes;
	private final OValContext context;
	private final String message;
	private transient Object validatedObject;
	private transient Object value;

	public ConstraintViolation(final String message, final Object validatedObject,
			final Object value, final OValContext context)
	{
		this.message = message;
		this.validatedObject = validatedObject;
		this.value = value;
		this.context = context;
		this.causes = null;
	}

	public ConstraintViolation(final String message, final Object validatedObject,
			final Object value, final OValContext context, final ConstraintViolation[] causes)
	{
		this.message = message;
		this.validatedObject = validatedObject;
		this.value = value;
		this.context = context;
		this.causes = causes;
	}

	/**
	 * @return the causes
	 */
	public ConstraintViolation[] getCauses()
	{
		return causes;
	}

	/**
	 * @return Returns the context where the constraint violation occured.
	 */
	public OValContext getContext()
	{
		return context;
	}

	/**
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @return the validatedObject
	 */
	public Object getValidatedObject()
	{
		return validatedObject;
	}

	/**
	 * @return Returns the value that was validated.
	 */
	public Object getValue()
	{
		return value;
	}

	/**
	 * 
	 * @param in
	 * @throws IOException
	 * @see http://java.sun.com/developer/technicalArticles/ALT/serialization/
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
			value = in.readObject();
		}
	}

	public String toString()
	{
		return getClass().getName() + ": " + message;
	}

	/**
	 * 
	 * @param out
	 * @see http://java.sun.com/developer/technicalArticles/ALT/serialization/
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
			LOG.warning("Field 'validatedObject' not serialized because the referenced object "
					+ validatedObject + " of type " + value.getClass()
					+ " does not implement Serializable.");

			// indicate validatedObject does not implement Serializable
			out.writeBoolean(false);
		}

		if (value instanceof Serializable)
		{
			// indicate value implements Serializable
			out.writeBoolean(true);
			out.writeObject(value);
		}
		else
		{
			LOG.warning("Field 'value' not serialized because the referenced object " + value
					+ " does not implement Serializable.");
			// indicate value does not implement Serializable
			out.writeBoolean(false);
		}
	}
}
