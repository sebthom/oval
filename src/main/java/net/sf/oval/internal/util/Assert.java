/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.oval.internal.util;

/**
 * @author Sebastian Thomschke
 */
public final class Assert
{
	private static RuntimeException _adjustStacktrace(final RuntimeException ex)
	{
		final StackTraceElement[] stack = ex.getStackTrace();
		final StackTraceElement[] newStack = new StackTraceElement[stack.length - 1];
		System.arraycopy(stack, 1, newStack, 0, stack.length - 1);
		ex.setStackTrace(newStack);
		return ex;
	}

	public static <T> void argumentNotEmpty(final String name, final String value) throws IllegalArgumentException
	{
		if (value == null) throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
		if (value.length() == 0)
			throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be empty"));
	}

	public static <T> void argumentNotEmpty(final String name, final T[] value) throws IllegalArgumentException
	{
		if (value == null) throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
		if (value.length == 0)
			throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be empty"));
	}

	public static void argumentNotNull(final String name, final Object value) throws IllegalArgumentException
	{
		if (value == null) throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
	}

	/**
	 * private constructor
	 */
	private Assert()
	{
		super();
	}
}
