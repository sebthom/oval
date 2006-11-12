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
package net.sf.oval.aspectj;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.WeakHashMap;

import net.sf.oval.ParameterNameResolver;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.utils.ReflectionUtils;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This class determines the names of constructor and method parameters based on the static JoinPoint fields added to the classes by the AspectJ compiler
 *  
 * @author Sebastian Thomschke
 */
public class ParameterNameResolverAspectJImpl implements ParameterNameResolver
{
	private final WeakHashMap<AccessibleObject, String[]> parameterNamesCache = new WeakHashMap<AccessibleObject, String[]>();

	public String[] getParameterNames(final Method method) throws ReflectionException
	{
		/*
		 * intentionally the following code is not synchronized
		 */
		String[] parameterNames = parameterNamesCache.get(method);
		if (parameterNames == null)
		{
			try
			{
				determineParamterNames(method.getDeclaringClass());
				parameterNames = parameterNamesCache.get(method);
			}
			catch (IllegalArgumentException e)
			{
				throw new ReflectionException("Cannot detemine parameter names for method "
						+ method, e);
			}
			catch (SecurityException e)
			{
				throw new ReflectionException("Cannot detemine parameter names for method "
						+ method, e);
			}
			catch (IllegalAccessException e)
			{
				throw new ReflectionException("Cannot detemine parameter names for method "
						+ method, e);
			}
		}

		if (parameterNames == null)
		{
			final int parameterCount = method.getParameterTypes().length;
			parameterNames = new String[parameterCount];
			for (int i = 0; i < parameterCount; i++)
			{
				parameterNames[i] = "parameter" + i;
			}
			parameterNamesCache.put(method, parameterNames);
		}
		return parameterNames;
	}

	public String[] getParameterNames(final Constructor constructor) throws ReflectionException
	{
		/*
		 * intentionally the following code is not synchronized
		 */
		String[] parameterNames = parameterNamesCache.get(constructor);
		if (parameterNames == null)
		{
			try
			{
				determineParamterNames(constructor.getDeclaringClass());
				parameterNames = parameterNamesCache.get(constructor);
			}
			catch (IllegalArgumentException e)
			{
				throw new ReflectionException("Cannot detemine parameter names for constructor "
						+ constructor, e);
			}
			catch (SecurityException e)
			{
				throw new ReflectionException("Cannot detemine parameter names for constructor "
						+ constructor, e);
			}
			catch (IllegalAccessException e)
			{
				throw new ReflectionException("Cannot detemine parameter names for constructor "
						+ constructor, e);
			}
		}

		if (parameterNames == null)
		{
			final int parameterCount = constructor.getParameterTypes().length;
			parameterNames = new String[parameterCount];
			for (int i = 0; i < parameterCount; i++)
			{
				parameterNames[i] = "parameter" + i;
			}
			parameterNamesCache.put(constructor, parameterNames);
		}
		return parameterNames;
	}

	private void determineParamterNames(final Class clazz) throws IllegalArgumentException,
			IllegalAccessException, SecurityException
	{
		for (final Field field : clazz.getDeclaredFields())
		{
			// search for static fields of type JoinPoint.StaticPart
			if (ReflectionUtils.isStatic(field) && field.getType() == JoinPoint.StaticPart.class)
			{
				// access the StaticPart object
				field.setAccessible(true);
				final JoinPoint.StaticPart staticPart = (JoinPoint.StaticPart) field.get(null);
				if (staticPart == null) break;

				if (staticPart.getSignature() instanceof ConstructorSignature)
				{
					final ConstructorSignature sig = (ConstructorSignature) staticPart
							.getSignature();
					final String[] parameterNames = sig.getParameterNames();

					final Constructor constr = sig.getConstructor();

					if (parameterNames.length > 0) parameterNamesCache.put(constr, parameterNames);
				}
				else if (staticPart.getSignature() instanceof MethodSignature)
				{
					final MethodSignature sig = (MethodSignature) staticPart.getSignature();
					final String[] parameterNames = sig.getParameterNames();

					final Method method = sig.getMethod();

					if (parameterNames.length > 0) parameterNamesCache.put(method, parameterNames);
				}
			}
		}
	}
}
