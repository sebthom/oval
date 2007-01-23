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
package net.sf.oval.exceptions;

import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class InvokingMethodFailedException extends ReflectionException
{
	private static final long serialVersionUID = 1L;

	private final OValContext context;
	private final Object validatedObject;

	public InvokingMethodFailedException(final String message, final Object validatedObject,
			final OValContext context, final Throwable cause)
	{
		super(message, cause);
		this.context = context;
		this.validatedObject = validatedObject;
	}

	/**
	 * @return Returns the context.
	 */
	public OValContext getContext()
	{
		return context;
	}

	/**
	 * @return the validatedObject
	 */
	public Object getValidatedObject()
	{
		return validatedObject;
	}
}