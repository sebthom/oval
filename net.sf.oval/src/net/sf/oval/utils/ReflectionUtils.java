package net.sf.oval.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class ReflectionUtils
{
	private static final Logger LOG = Logger.getLogger(ReflectionUtils.class.getName());

	public static Field getFieldForSetter(final Method setter)
	{
		if (!isSetter(setter)) return null;

		final Class< ? >[] methodParameterTypes = setter.getParameterTypes();
		final String methodName = setter.getName();
		final Class< ? > clazz = setter.getDeclaringClass();

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
			if (LOG.isLoggable(Level.FINE))
			{
				LOG.log(Level.FINE, "Field not found", e);
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
				if (LOG.isLoggable(Level.FINE))
				{
					LOG.log(Level.FINE, "Field not found", ex);
				}
			}
		}
		
		return field;
	}

	/**
	 * determines if a method is a JavaBean style getter method
	 */
	public static boolean isGetter(final Method method)
	{
		return (method.getParameterTypes().length == 0)
				&& (method.getName().startsWith("is") || method.getName().startsWith("get"));
	}

	/**
	 * determines if a method is a JavaBean style setter method
	 */
	public static boolean isSetter(final Method method)
	{
		final Class< ? >[] methodParameterTypes = method.getParameterTypes();

		// check if method has exactly one parameter
		if (methodParameterTypes.length != 1) return false;

		final String methodName = method.getName();
		final int methodNameLen = methodName.length();

		// check if the method's name starts with setXXX
		if (!methodName.startsWith("set") || methodNameLen <= 3) return false;

		return true;
	}

	private ReflectionUtils()
	{
	// do nothing
	}
}
