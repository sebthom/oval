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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.WeakHashMap;

import net.sf.oval.ParameterNameResolver;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.utils.WeakHashSet;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * This class determines the names of constructor and method parameters based on the static JoinPoint fields added to the classes by the AspectJ compiler 
 * @author Sebastian Thomschke
 * @version $Revision: 1.0 $
 */
public class ParameterNameResolverAspectJImpl implements ParameterNameResolver
{
	private final WeakHashMap<Constructor, String[]> constructorParameterNames = new WeakHashMap<Constructor, String[]>();
	private final WeakHashMap<Method, String[]> methodParameterNames = new WeakHashMap<Method, String[]>();
	private final WeakHashSet<Class> inspectedClasses = new WeakHashSet<Class>();

	public synchronized String[] getParameterNames(Method method) throws ReflectionException
	{
		String[] parameterNames = methodParameterNames.get(method);
		if (parameterNames == null)
		{
			try
			{
				determineParamterNames(method.getDeclaringClass());
				parameterNames = methodParameterNames.get(method);
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
			try
			{
				determineParamterNames(constructor.getDeclaringClass());
				parameterNames = constructorParameterNames.get(constructor);
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

	private void determineParamterNames(Class clazz) throws IllegalArgumentException,
			IllegalAccessException, SecurityException
	{
		for (final Field field : clazz.getDeclaredFields())
		{
			// search for static fields of type JoinPoint.StaticPart
			if (((field.getModifiers() & Modifier.STATIC) != 0)
					&& field.getType() == JoinPoint.StaticPart.class)
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

					if (parameterNames.length > 0)
						constructorParameterNames.put(constr, parameterNames);
				}
				else if (staticPart.getSignature() instanceof MethodSignature)
				{
					final MethodSignature sig = (MethodSignature) staticPart.getSignature();
					final String[] parameterNames = sig.getParameterNames();

					final Method method = sig.getMethod();

					if (parameterNames.length > 0)
						methodParameterNames.put(method, parameterNames);
				}
			}
		}

		inspectedClasses.add(clazz);
	}
}
