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
package net.sf.oval.contexts;

import java.lang.reflect.Constructor;

import net.sf.oval.utils.SerializableConstructor;
import net.sf.oval.utils.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class ConstructorParameterContext extends OValContext
{
	private static final long serialVersionUID = 1L;

	private final SerializableConstructor constructor;
	private final int parameterIndex;
	private final String parameterName;

	/**
	 * 
	 * @param constructor
	 * @param parameterIndex
	 */
	public ConstructorParameterContext(final Constructor< ? > constructor,
			final int parameterIndex, final String parameterName)
	{
		this.constructor = SerializableConstructor.getInstance(constructor);
		this.parameterIndex = parameterIndex;
		this.parameterName = parameterName;
	}

	/**
	 * @return Returns the constructor.
	 */
	public Constructor<?> getConstructor()
	{
		return constructor.getConstructor();
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

	public String toString()
	{
		return constructor.getDeclaringClass().getName()
				+ "("
				+ StringUtils.implode(constructor.getParameterTypes(), ",")
				+ ") Parameter "
				+ parameterIndex
				+ (parameterName == null || parameterName.length() == 0 ? "" : " (" + parameterName
						+ ")");
	}
}
