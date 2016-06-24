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

import java.lang.reflect.Method;

import net.sf.oval.exception.ConstraintsViolatedException;
import net.sf.oval.exception.InvokingMethodFailedException;

/**
 * @author Sebastian Thomschke
 */
public final class MethodInvocationCommand
{
	private final Object target;
	private final Method method;
	private final Object[] args;

	public MethodInvocationCommand(final Object target, final Method method, final Object[] args)
	{
		this.target = target;
		this.method = method;
		this.args = args;
	}

	public Object execute() throws InvokingMethodFailedException, ConstraintsViolatedException
	{
		return ReflectionUtils.invokeMethod(method, target, args);
	}
}
