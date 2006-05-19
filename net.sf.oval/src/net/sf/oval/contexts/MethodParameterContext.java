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
package net.sf.oval.contexts;

import java.lang.reflect.Method;

import net.sf.oval.utils.StringUtils;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.3 $
 */
public class MethodParameterContext extends OValContext
{
	private final Method method;
	private final int parameterIndex;
	private final String parameterName;

	public MethodParameterContext(final Method method, final int parameterIndex,
			final String parameterName)
	{
		this.method = method;
		this.parameterIndex = parameterIndex;
		this.parameterName = parameterName;
	}

	/**
	 * @return Returns the method.
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * @return Returns the parameterIndex.
	 */
	public int getParameterIndex()
	{
		return parameterIndex;
	}

	public String toString()
	{
		return method.getDeclaringClass().getName()
				+ "."
				+ method.getName()
				+ "("
				+ StringUtils.implode(method.getParameterTypes(), ",")
				+ ") Parameter "
				+ parameterIndex
				+ (parameterName == null || parameterName.length() == 0 ? "" : " (" + parameterName
						+ ")");
	}

	/**
	 * @return the parameterName
	 */
	public String getParameterName()
	{
		return parameterName;
	}

}
