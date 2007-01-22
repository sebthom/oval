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

import java.io.IOException;

/**
 * @author Sebastian Thomschke
 */
public class NestableIOException extends IOException
{
	private static final long serialVersionUID = 1L;

	private final Throwable cause;

	public NestableIOException(final Throwable cause)
	{
		super((cause == null ? null : cause.toString()));
		this.cause = cause;
	}

	@Override
	public Throwable getCause()
	{
		return cause;
	}
}
