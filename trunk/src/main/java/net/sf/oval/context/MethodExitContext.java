/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval.context;

import java.lang.reflect.Method;

import net.sf.oval.internal.util.SerializableMethod;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class MethodExitContext extends OValContext
{
	private static final long serialVersionUID = 1L;

	private final SerializableMethod method;

	public MethodExitContext(final Method method)
	{
		this.method = SerializableMethod.getInstance(method);
	}

	/**
	 * @return Returns the method.
	 */
	public Method getMethod()
	{
		return method.getMethod();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return method.getDeclaringClass().getName() + "." + method.getName() + "("
				+ StringUtils.implode(method.getParameterTypes(), ",") + ")";
	}
}
