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

import net.sf.oval.contexts.OValContext;

/**
 * An instance of this class provides detailed information about a constraint validation.
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.3 $
 */
public class ConstraintViolation
{
	private final Check check;
	private final OValContext context;
	private final String message;
	private final Object validatedObject;
	private final Object value;

	public ConstraintViolation(final String message, final Object validatedObject,
			final Object value, final OValContext context, final Check check)
	{
		this.message = message;
		this.validatedObject = validatedObject;
		this.value = value;
		this.context = context;
		this.check = check;
	}

	/**
	 * @return Returns the check.
	 */
	public Check getCheck()
	{
		return check;
	}

	/**
	 * @return Returns the context.
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
	 * @return Returns the value.
	 */
	public Object getValue()
	{
		return value;
	}

	public String toString()
	{
		return getClass().getName() + ": " + message;
	}
}
