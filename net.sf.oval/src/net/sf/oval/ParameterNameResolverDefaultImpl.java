/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2006 Sebastian
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

import net.sf.oval.exceptions.ReflectionException;

/**
 * This implementation determines the names of constructor and method parameters by simply enumerating them based on there index 
 * @author Sebastian Thomschke
 * @version $Revision: 1.0 $
 */
public class ParameterNameResolverDefaultImpl implements ParameterNameResolver
{
	private final WeakHashMap<Constructor, String[]> constructorParameterNames = new WeakHashMap<Constructor, String[]>();
	private final WeakHashMap<Method, String[]> methodParameterNames = new WeakHashMap<Method, String[]>();

	public synchronized String[] getParameterNames(Method method) throws ReflectionException
	{
		String[] parameterNames = methodParameterNames.get(method);
		if (parameterNames == null)
		{
			int parameterCount = method.getParameterTypes().length;
			parameterNames = new String[parameterCount];
			for (int i = 0; i < parameterCount; i++)
			{
				parameterNames[i] = "parameter" + i;
			}
			methodParameterNames.put(method, parameterNames);
		}
		return parameterNames;
	}

	public String[] getParameterNames(Constructor constructor) throws ReflectionException
	{
		String[] parameterNames = constructorParameterNames.get(constructor);
		if (parameterNames == null)
		{
			int parameterCount = constructor.getParameterTypes().length;
			parameterNames = new String[parameterCount];
			for (int i = 0; i < parameterCount; i++)
			{
				parameterNames[i] = "parameter" + i;
			}
			constructorParameterNames.put(constructor, parameterNames);
		}
		return parameterNames;
	}
}
