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
package net.sf.oval.internal.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.exception.AccessingFieldValueFailedException;
import net.sf.oval.exception.InvokingMethodFailedException;

/**
 * @author Sebastian Thomschke
 */
public final class ReflectionUtils
{
	private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());

	/**
	 * @return the field or null if the field does not exist
	 */
	public static Field getField(final Class clazz, final String fieldName)
	{
		try
		{
			return clazz.getDeclaredField(fieldName);
		}
		catch (NoSuchFieldException e)
		{
			return null;
		}
	}

	/**
	 * @param setter
	 * @return Returns the corresponding field for a setter method. Returns null if the method is not a JavaBean style setter or the field could not be located.
	 */
	public static Field getFieldForSetter(final Method setter)
	{
		if (!isSetter(setter)) return null;

		final Class[] methodParameterTypes = setter.getParameterTypes();
		final String methodName = setter.getName();
		final Class clazz = setter.getDeclaringClass();

		// calculate the corresponding field name based on the name of the setter method (e.g. method setName() => field name)
		String fieldName = methodName.substring(3, 4).toLowerCase();
		if (methodName.length() > 4)
		{
			fieldName += methodName.substring(4);
		}

		Field field = null;
		try
		{
			field = clazz.getDeclaredField(fieldName);

			// check if field and method parameter are of the same type
			if (!field.getType().equals(methodParameterTypes[0]))
			{
				LOG.warning("Found field <" + fieldName + "> in class <" + clazz.getName()
						+ ">that matches setter <" + methodName
						+ "> name, but mismatches parameter type.");
				field = null;
			}
		}
		catch (final NoSuchFieldException e)
		{
			if (LOG.isLoggable(Level.FINER))
			{
				LOG.log(Level.FINER, "Field not found", e);
			}
		}

		// if method parameter type is boolean then check if a field with name isXXX exists (e.g. method setEnabled() => field isEnabled)
		if (field == null
				&& (methodParameterTypes[0].equals(boolean.class) || methodParameterTypes[0]
						.equals(Boolean.class)))
		{
			fieldName = "is" + methodName.substring(3);

			try
			{
				field = clazz.getDeclaredField(fieldName);

				// check if found field is of boolean or Boolean
				if (!field.getType().equals(boolean.class) && field.getType().equals(Boolean.class))
				{
					LOG.warning("Found field <" + fieldName + "> that matches setter <"
							+ methodName + "> name, but mismatches parameter type.");
					field = null;
				}
			}
			catch (NoSuchFieldException ex)
			{
				if (LOG.isLoggable(Level.FINER))
				{
					LOG.log(Level.FINER, "Field not found", ex);
				}
			}
		}

		return field;
	}

	public static Field getFieldRecursive(final Class clazz, final String fieldName)
	{
		final Field f = getField(clazz, fieldName);
		if (f != null) return f;

		final Class superclazz = clazz.getSuperclass();
		if (superclazz == null) return null;

		return getFieldRecursive(superclazz, fieldName);
	}

	public static Object getFieldValue(final Field field, final Object obj)
			throws AccessingFieldValueFailedException
	{
		try
		{
			if (!field.isAccessible()) field.setAccessible(true);
			return field.get(obj);
		}
		catch (Exception ex)
		{
			throw new AccessingFieldValueFailedException(field.getName(), obj, new FieldContext(
					field), ex);
		}
	}

	/**
	 * @return the method or null if the method does not exist
	 */
	public static Method getMethod(final Class<?> clazz, final String methodName,
			final Class<?>... parameterTypes) throws SecurityException
	{
		try
		{
			return clazz.getDeclaredMethod(methodName, parameterTypes);
		}
		catch (NoSuchMethodException e)
		{
			return null;
		}
	}

	public static Method getSuperMethod(final Method method)
	{
		// static methods cannot be overridden
		if (isStatic(method)) return null;

		final String methodName = method.getName();
		final Class<?>[] parameterTypes = method.getParameterTypes();

		Class<?> currentClass = method.getDeclaringClass();

		while (currentClass != null && currentClass != Object.class)
		{
			currentClass = currentClass.getSuperclass();

			final Method m = getMethod(currentClass, methodName, parameterTypes);

			if (m != null) return m;
		}
		return null;
	}

	public static String guessFieldName(final Method getter)
	{
		String fieldName = getter.getName();

		if (fieldName.startsWith("get") && fieldName.length() > 3)
		{
			fieldName = fieldName.substring(3);
			if (fieldName.length() == 1)
				fieldName = fieldName.toLowerCase();
			else
				fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
		}
		else if (fieldName.startsWith("is") && fieldName.length() > 2)
		{
			fieldName = fieldName.substring(2);
			if (fieldName.length() == 1)
				fieldName = fieldName.toLowerCase();
			else
				fieldName = Character.toLowerCase(fieldName.charAt(0)) + fieldName.substring(1);
		}

		return fieldName;
	}

	public static boolean hasField(final Class clazz, final String fieldName)
	{
		return getField(clazz, fieldName) != null;
	}

	public static boolean hasMethod(final Class<?> clazz, final String methodName,
			final Class<?>... parameterTypes)
	{
		return getMethod(clazz, methodName, parameterTypes) != null;
	}

	/**
	 * 
	 * @param method the method to invoke
	 * @param obj the object on which to invoke the method
	 * @param args the method arguments
	 * @return the return value of the invoked method
	 * @throws InvokingMethodFailedException
	 */
	public static Object invokeMethod(final Method method, final Object obj, final Object... args)
			throws InvokingMethodFailedException
	{
		try
		{
			if (!method.isAccessible()) method.setAccessible(true);
			return method.invoke(obj, args);
		}
		catch (final Exception ex)
		{
			throw new InvokingMethodFailedException("Executing method " + method.getName()
					+ " failed.", obj, new MethodReturnValueContext(method), ex);
		}
	}

	public static boolean isClassPresent(final String className)
	{
		try
		{
			Class.forName(className);
			return true;
		}
		catch (ClassNotFoundException e)
		{
			return false;
		}
	}

	public static boolean isFinal(final Field field)
	{
		return (field.getModifiers() & Modifier.FINAL) != 0;
	}

	public static boolean isFinal(final Method method)
	{
		return (method.getModifiers() & Modifier.FINAL) != 0;
	}

	/**
	 * determines if a method is a JavaBean style getter method
	 */
	public static boolean isGetter(final Method method)
	{
		return (method.getParameterTypes().length == 0)
				&& (method.getName().startsWith("is") || method.getName().startsWith("get"));
	}

	public static boolean isPrivate(final Field field)
	{
		return (field.getModifiers() & Modifier.PRIVATE) != 0;
	}

	public static boolean isPrivate(final Method method)
	{
		return (method.getModifiers() & Modifier.PRIVATE) != 0;
	}

	public static boolean isProtected(final Field field)
	{
		return (field.getModifiers() & Modifier.PROTECTED) != 0;
	}

	public static boolean isProtected(final Method method)
	{
		return (method.getModifiers() & Modifier.PROTECTED) != 0;
	}

	/**
	 * determines if a method is a JavaBean style setter method
	 */
	public static boolean isSetter(final Method method)
	{
		final Class[] methodParameterTypes = method.getParameterTypes();

		// check if method has exactly one parameter
		if (methodParameterTypes.length != 1) return false;

		final String methodName = method.getName();
		final int methodNameLen = methodName.length();

		// check if the method's name starts with setXXX
		if (methodNameLen < 4 || !methodName.startsWith("set")) return false;

		return true;
	}

	public static boolean isStatic(final Field field)
	{
		return (field.getModifiers() & Modifier.STATIC) != 0;
	}

	public static boolean isStatic(final Method method)
	{
		return (method.getModifiers() & Modifier.STATIC) != 0;
	}

	public static boolean isTransient(final Field field)
	{
		return (field.getModifiers() & Modifier.TRANSIENT) != 0;
	}

	/**
	 * determines if a method is a void method
	 */
	public static boolean isVoidMethod(final Method method)
	{
		return method.getReturnType() == void.class;
	}

	private ReflectionUtils()
	{
	// do nothing
	}
}
