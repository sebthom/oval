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
package net.sf.oval;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.WeakHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.contexts.ConstructorParameterContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.MethodParameterContext;
import net.sf.oval.contexts.MethodReturnValueContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.AccessingFieldValueFailedException;
import net.sf.oval.exceptions.ConstraintAnnotationNotPresentException;
import net.sf.oval.exceptions.InvokingGetterFailedException;

import org.aspectj.lang.reflect.ConstructorSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.10 $
 */
public final class Validator
{
	static AnnotationCheckLoader annotationCheckLoader = new AnnotationCheckLoaderImpl();

	private static final WeakHashMap<Class, ClassChecks> checksByClass = new WeakHashMap<Class, ClassChecks>();

	private static final Logger LOG = Logger.getLogger(Validator.class.getName());

	private static final HashMap<ResourceBundle, ArrayList<String>> messageBundleKeys = new HashMap<ResourceBundle, ArrayList<String>>();
	private static final LinkedList<ResourceBundle> messageBundles = new LinkedList<ResourceBundle>();

	static
	{
		// add the message bundle for the pre-built constraints in the default locale
		addMessageBundle(ResourceBundle.getBundle("net/sf/oval/constraints/Messages"));
	}

	public static void addCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws ConstraintAnnotationNotPresentException
	{
		Class clazz = constructor.getDeclaringClass();
		ClassChecks checks = getClassChecks(clazz);
		checks.addCheck(constructor, parameterIndex, check);
	}

	public static void addCheck(final Field field, final Check check)
	{
		Class clazz = field.getDeclaringClass();
		ClassChecks checks = getClassChecks(clazz);
		checks.addCheck(field, check);
	}

	public static void addCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstraintAnnotationNotPresentException
	{
		Class clazz = method.getDeclaringClass();
		ClassChecks checks = getClassChecks(clazz);
		checks.addCheck(method, parameterIndex, check);
	}

	/**
	 * 
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it was already registered
	 */
	public static boolean addMessageBundle(final ResourceBundle messageBundle)
	{
		if (messageBundles.contains(messageBundle)) return false;

		messageBundles.addFirst(messageBundle);
		final ArrayList<String> keys = new ArrayList<String>();

		for (final Enumeration<String> keysEnum = messageBundle.getKeys(); keysEnum
				.hasMoreElements();)
		{
			keys.add(keysEnum.nextElement());
		}

		messageBundleKeys.put(messageBundle, keys);

		return true;
	}

	/**
	 * @return the checkLoader
	 */
	public static AnnotationCheckLoader getAnnotationCheckLoader()
	{
		return annotationCheckLoader;
	}

	private static ClassChecks getClassChecks(final Class clazz)
	{
		ClassChecks classConstraints = checksByClass.get(clazz);
		if (classConstraints == null)
		{
			synchronized (checksByClass)
			{
				classConstraints = checksByClass.get(clazz);

				if (classConstraints == null)
				{
					classConstraints = new ClassChecks(clazz);
				}
				checksByClass.put(clazz, classConstraints);
			}
		}
		return classConstraints;
	}

	private static Object getFieldValue(final Object validatedObject, final Field field)
			throws AccessingFieldValueFailedException
	{
		try
		{
			if (!field.isAccessible()) field.setAccessible(true);
			return field.get(validatedObject);
		}
		catch (IllegalArgumentException ex)
		{
			LOG.log(Level.SEVERE, "Accessing value of field " + field.getName() + "failed.", ex);
			throw new AccessingFieldValueFailedException("Accessing value of field "
					+ field.getName() + "failed.", validatedObject, new FieldContext(field), ex);
		}
		catch (IllegalAccessException ex)
		{
			LOG.log(Level.SEVERE, "Accessing value of field " + field.getName() + "failed.", ex);
			throw new AccessingFieldValueFailedException("Accessing value of field "
					+ field.getName() + "failed.", validatedObject, new FieldContext(field), ex);
		}
	}

	private static Object getGetterValue(final Object validatedObject, final Method getter)
			throws InvokingGetterFailedException
	{
		try
		{
			return getter.invoke(validatedObject, (Object[]) null);
		}
		catch (Exception ex)
		{
			LOG.log(Level.SEVERE, "Executing getter method " + getter.getName() + " failed.", ex);
			throw new InvokingGetterFailedException("Executing getter method " + getter.getName()
					+ " failed.", validatedObject, new MethodReturnValueContext(getter), ex);
		}
	}

	/**
	 * 
	 * @param messageBundle
	 * @return true if the bundle was registered and false if it wasn't registered
	 */
	public static boolean removeMessageBundle(final ResourceBundle messageBundle)
	{
		if (!messageBundles.contains(messageBundle)) return false;

		messageBundles.remove(messageBundle);
		return true;
	}

	private static String renderMessage(final OValContext context, final Object value,
			final Check check)
	{
		String messageKey = check.getMessage();

		for (final ResourceBundle bundle : messageBundles)
		{
			final ArrayList<String> keys = messageBundleKeys.get(bundle);
			if (keys.contains(messageKey))
			{
				messageKey = bundle.getString(messageKey);
				break;
			}
		}

		// if no place holders are in the message return just return it
		if (messageKey.indexOf('{') == -1) return messageKey;

		final LinkedList<Object> args = new LinkedList<Object>();
		args.addLast(context);
		args.addLast(value);
		final String[] messageValues = check.getMessageValues();
		if (messageValues != null)
		{
			for (final String val : messageValues)
			{
				args.addLast(val);
			}
		}

		return MessageFormat.format(messageKey, args.toArray());
	}

	/**
	 * @param checkLoader the checkLoader to set
	 */
	public static void setAnnotationCheckLoader(final AnnotationCheckLoader checkLoader)
	{
		Validator.annotationCheckLoader = checkLoader;
	}

	/**
	 * validates the field and getter constrains of the given object
	 * 
	 * @param validatedObject
	 * @return  a list with the detected constraint violations. if no violations are detected an empty list is returned
	 */
	public static List<ConstraintViolation> validate(final Object validatedObject)
	{
		final ArrayList<ConstraintViolation> violations = new ArrayList<ConstraintViolation>();
		validateObject(validatedObject, validatedObject.getClass(), violations);
		return violations;
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	static List<ConstraintViolation> validateConstructorParameters(final Object validatedObject,
			final ConstructorSignature constructorSignature, final Object[] parameters)
	{
		final Constructor constructor = constructorSignature.getConstructor();
		final String[] parameterNames = constructorSignature.getParameterNames();
		final ClassChecks classConstraints = getClassChecks(constructor.getDeclaringClass());

		final HashMap<Integer, HashSet<Check>> parameterChecks = classConstraints.checksByConstructorParameter
				.get(constructor);

		if (parameterChecks == null) return null;

		final ArrayList<ConstraintViolation> violations = new ArrayList<ConstraintViolation>();
		for (int i = 0; i < parameters.length; i++)
		{
			final HashSet<Check> checks = parameterChecks.get(i);
			final Object valueToValidate = parameters[i];

			if (checks != null)
			{
				for (final Check check : checks)
				{
					if (!check.isSatisfied(validatedObject, valueToValidate))
					{
						final ConstructorParameterContext context = new ConstructorParameterContext(
								constructor, i, parameterNames[i]);
						final String errorMessage = renderMessage(context, valueToValidate, check);
						violations.add(new ConstraintViolation(errorMessage, validatedObject,
								valueToValidate, context, check));
					}
				}
			}
		}
		return violations.size() == 0 ? null : violations;
	}

	private static void validateField(final Object validatedObject, final Field field,
			final List<ConstraintViolation> violations)
	{
		final ClassChecks cc = getClassChecks(field.getDeclaringClass());

		final HashSet<Check> checks = cc.checksByField.get(field);

		if (checks != null)
		{
			final Object valueToValidate = getFieldValue(validatedObject, field);

			for (final Check check : checks)
			{
				if (!check.isSatisfied(validatedObject, valueToValidate))
				{
					final FieldContext context = new FieldContext(field);
					final String errorMessage = renderMessage(context, valueToValidate, check);
					violations.add(new ConstraintViolation(errorMessage, validatedObject,
							valueToValidate, context, check));
				}
			}
		}
	}

	private static void validateGetter(final Object validatedObject, final Method getter,
			final List<ConstraintViolation> violations)
	{
		final ClassChecks cc = getClassChecks(getter.getDeclaringClass());

		final HashSet<Check> checks = cc.checksByGetter.get(getter);

		if (checks != null)
		{
			final Object valueToValidate = getGetterValue(validatedObject, getter);

			for (final Check check : checks)
			{
				if (!check.isSatisfied(validatedObject, valueToValidate))
				{
					final MethodReturnValueContext context = new MethodReturnValueContext(getter);
					final String errorMessage = renderMessage(context, valueToValidate, check);
					violations.add(new ConstraintViolation(errorMessage, validatedObject,
							valueToValidate, context, check));
				}
			}
		}
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	static List<ConstraintViolation> validateMethodReturnValue(final Object validatedObject,
			final MethodSignature methodSignature, final Object methodReturnValue)
	{
		final Method method = methodSignature.getMethod();

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final HashSet<Check> checks = cc.checksByMethod.get(method);

		if (checks == null) return null;

		final ArrayList<ConstraintViolation> violations = new ArrayList<ConstraintViolation>();

		for (final Check check : checks)
		{
			if (!check.isSatisfied(validatedObject, methodReturnValue))
			{
				final MethodReturnValueContext context = new MethodReturnValueContext(method);
				final String errorMessage = renderMessage(context, methodReturnValue, check);
				violations.add(new ConstraintViolation(errorMessage, validatedObject,
						methodReturnValue, context, check));
			}
		}
		return violations.size() == 0 ? null : violations;
	}

	/**
	 * used by ConstraintsEnforcer
	 * 
	 * @return null if no violation, otherwise a list
	 */
	static List<ConstraintViolation> validateMethodParameters(final Object validatedObject,
			final MethodSignature methodSignature, final Object[] parameters)
	{
		final Method method = methodSignature.getMethod();
		final String[] parameterNames = methodSignature.getParameterNames();

		final ClassChecks cc = getClassChecks(method.getDeclaringClass());

		final HashMap<Integer, HashSet<Check>> parameterChecks = cc.checksByMethodParameter
				.get(method);

		// check if the method has any parameter checks at all
		if (parameterChecks == null) return null;

		final ArrayList<ConstraintViolation> violations = new ArrayList<ConstraintViolation>();

		for (int i = 0; i < parameters.length; i++)
		{
			final HashSet<Check> checks = parameterChecks.get(i);
			final Object valueToValidate = parameters[i];

			if (checks != null)
			{
				for (final Check check : checks)
				{
					if (!check.isSatisfied(validatedObject, valueToValidate))
					{
						final MethodParameterContext context = new MethodParameterContext(method,
								i, parameterNames[i]);
						final String errorMessage = renderMessage(context, valueToValidate, check);
						violations.add(new ConstraintViolation(errorMessage, validatedObject,
								valueToValidate, context, check));
					}
				}
			}
		}
		return violations.size() == 0 ? null : violations;
	}

	private static void validateObject(final Object validatedObject, final Class< ? > clazz,
			final List<ConstraintViolation> violations)
	{
		if (clazz == Object.class) return;

		final ClassChecks cc = getClassChecks(clazz);

		// validate field constraints
		for (final Field field : cc.constrainedFields)
		{
			validateField(validatedObject, field, violations);
		}

		// validate constraints on getter methods
		for (final Method getter : cc.constrainedGetters)
		{
			validateGetter(validatedObject, getter, violations);
		}

		// if the super class is annotated to be validatable also validate it against the object
		validateObject(validatedObject, clazz.getSuperclass(), violations);
	}

	private Validator()
	{}
}
