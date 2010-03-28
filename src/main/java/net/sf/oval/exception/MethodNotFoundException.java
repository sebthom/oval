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
package net.sf.oval.exception;

/**
 * @author Sebastian Thomschke
 */
public class MethodNotFoundException extends ReflectionException
{
	private static final long serialVersionUID = 1L;

	public MethodNotFoundException(final String message)
	{
		super(message);
	}
	
	public MethodNotFoundException(final String message, final Throwable cause)
	{
		super(message, cause);
	}
	
	public MethodNotFoundException(final Throwable cause)
	{
		super(cause);
	}
}