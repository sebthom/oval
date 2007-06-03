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
package net.sf.oval.context;

import java.lang.reflect.Method;

import net.sf.oval.internal.MessageResolverHolder;
import net.sf.oval.internal.util.SerializableMethod;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class MethodParameterContext extends OValContext
{
	private static final long serialVersionUID = 1L;

	private final SerializableMethod method;
	private final int parameterIndex;
	private final String parameterName;

	public MethodParameterContext(final Method method, final int parameterIndex,
			final String parameterName)
	{
		this.method = SerializableMethod.getInstance(method);
		this.parameterIndex = parameterIndex;
		this.parameterName = parameterName == null ? "param" + parameterIndex : parameterName;
	}

	/**
	 * @return Returns the method.
	 */
	public Method getMethod()
	{
		return method.getMethod();
	}

	/**
	 * @return Returns the parameterIndex.
	 */
	public int getParameterIndex()
	{
		return parameterIndex;
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName()
	{
		return parameterName;
	}

	@Override
	public String toString()
	{
		return method.getDeclaringClass().getName()
				+ "."
				+ method.getName()
				+ "("
				+ StringUtils.implode(method.getParameterTypes(), ",")
				+ ") "
				+ MessageResolverHolder.getMessageResolver().getMessage(
						"net.sf.oval.context.MethodParameterContext.parameter")
				+ " "
				+ parameterIndex
				+ (parameterName == null || parameterName.length() == 0 ? "" : " (" + parameterName
						+ ")");
	}
}
