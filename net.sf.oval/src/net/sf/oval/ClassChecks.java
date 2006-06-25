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

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.Constraint;
import net.sf.oval.constraints.FieldConstraintsCheck;
import net.sf.oval.contexts.ClassContext;
import net.sf.oval.exceptions.ConstraintAnnotationNotPresentException;
import net.sf.oval.exceptions.ReflectionException;

/**
 * This class holds information about all the constraints defined for a class
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.10 $
 */
final class ClassChecks
{
	private static final Logger LOG = Logger.getLogger(ClassChecks.class.getName());

	/**
	 * checks on constructors' parameter values
	 */
	final HashMap<Constructor, HashMap<Integer, HashSet<Check>>> checksByConstructorParameter = new HashMap<Constructor, HashMap<Integer, HashSet<Check>>>();

	/**
	 * checks on fields' value
	 */
	final HashMap<Field, HashSet<Check>> checksByField = new HashMap<Field, HashSet<Check>>();

	/**
	 * checks on getter methods' return value
	 */
	final HashMap<Method, HashSet<Check>> checksByGetter = new HashMap<Method, HashSet<Check>>();

	/**
	 * checks on parameterized methods' return value
	 */
	final HashMap<Method, HashSet<Check>> checksByMethod = new HashMap<Method, HashSet<Check>>();

	/**
	 * checks on methods' parameter values
	 */
	final HashMap<Method, HashMap<Integer, HashSet<Check>>> checksByMethodParameter = new HashMap<Method, HashMap<Integer, HashSet<Check>>>();

	final HashSet<Field> constrainedFields = new HashSet<Field>();

	final HashSet<Method> constrainedGetters = new HashSet<Method>();
	final HashSet<Method> constrainedMethods = new HashSet<Method>();
	final HashSet<Constructor> constrainedParameterizedConstructors = new HashSet<Constructor>();

	final HashSet<Method> constrainedParameterizedMethods = new HashSet<Method>();
	final Constrained constrainedAnnotation;

	final Class clazz;

	final Validator validator;

	@SuppressWarnings("unchecked")
	ClassChecks(final Class clazz, final Validator validator) throws ReflectionException
	{
		if (LOG.isLoggable(Level.FINE)) LOG.fine("Trying to load checks for class " + clazz);

		this.clazz = clazz;
		this.validator = validator;

		constrainedAnnotation = (Constrained) clazz.getAnnotation(Constrained.class);

		if (constrainedAnnotation == null)
		{
			if (LOG.isLoggable(Level.FINE))
			{
				LOG.log(Level.FINE, clazz.getName() + ": @" + Constrained.class.getName()
						+ " annotation not present.", new ConstraintAnnotationNotPresentException(
						"@" + Constrained.class.getName() + " annotation not present.",
						new ClassContext(clazz)));
			}
		}

		try
		{
			setupChecksByField(); // field checks must be initiated first!
			setupChecksByConstructorParameters();
			setupChecksByMethodParameters();
			setupChecksByMethod();
		}
		catch (final SecurityException e)
		{
			throw new ReflectionException("Cannot load checks for class " + clazz.getName(), e);
		}
	}

	synchronized void addCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws ConstraintAnnotationNotPresentException
	{
		// check of the @Constrained class level annotation is present
		if (constrainedAnnotation == null)
			throw new ConstraintAnnotationNotPresentException(
					"Cannot apply constructor parameter constraints to class " + clazz.getName()
							+ ".  @" + Constrained.class.getName() + " annotation not present.",
					new ClassContext(clazz));

		// retrieve the currently registered checks for all parameters of the specified constructor
		HashMap<Integer, HashSet<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null)
		{
			checksOfConstructorByParameter = new HashMap<Integer, HashSet<Check>>();
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		HashSet<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null)
		{
			checksOfConstructorParameter = new HashSet<Check>();
			checksOfConstructorByParameter.put(parameterIndex, checksOfConstructorParameter);
		}

		checksOfConstructorParameter.add(check);
	}

	synchronized void addCheck(final Field field, final Check check)
	{
		HashSet<Check> checksOfField = checksByField.get(field);
		if (checksOfField == null)
		{
			checksOfField = new HashSet<Check>();
			checksByField.put(field, checksOfField);
			constrainedFields.add(field);
		}
		checksOfField.add(check);
	}

	synchronized void addCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstraintAnnotationNotPresentException
	{
		// check of the @Constrained class level annotation is present 
		if (constrainedAnnotation == null)
			throw new ConstraintAnnotationNotPresentException(
					"Cannot apply method parameter constraints to class " + clazz.getName()
							+ ".  @" + Constrained.class.getName() + " annotation not present.",
					new ClassContext(clazz));

		// retrieve the currently registered checks for all parameters of the specified method
		HashMap<Integer, HashSet<Check>> checksOfMethodByParameter = checksByMethodParameter
				.get(method);
		if (checksOfMethodByParameter == null)
		{
			checksOfMethodByParameter = new HashMap<Integer, HashSet<Check>>();
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		HashSet<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null)
		{
			checksOfMethodParameter = new HashSet<Check>();
			checksOfMethodByParameter.put(parameterIndex, checksOfMethodParameter);
		}

		checksOfMethodParameter.add(check);
	}

	private <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> loadCheck(
			final ConstraintAnnotation constraintAnnotation) throws ReflectionException
	{
		final Constraint constraint = constraintAnnotation.annotationType().getAnnotation(
				Constraint.class);
		Class checkClass = constraint.check();

		try
		{
			// instantiate the appropriate check for the found constraint
			@SuppressWarnings("unchecked")
			AnnotationCheck<ConstraintAnnotation> check = (AnnotationCheck<ConstraintAnnotation>) checkClass
					.newInstance();
			check.configure(constraintAnnotation);
			return check;
		}
		catch (Exception e)
		{
			throw new ReflectionException("Cannot load check " + checkClass.getName(), e);
		}
	}

	synchronized void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws ConstraintAnnotationNotPresentException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		HashMap<Integer, HashSet<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		{
			checksOfConstructorByParameter = new HashMap<Integer, HashSet<Check>>();
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		HashSet<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null) return;

		checksOfConstructorParameter.remove(check);

		if (checksOfConstructorParameter.size() == 0)
		{
			checksOfConstructorByParameter.remove(parameterIndex);
			if (checksOfConstructorByParameter.size() == 0)
				constrainedParameterizedConstructors.remove(constructor);
		}
	}

	synchronized void removeCheck(final Field field, final Check check)
	{
		HashSet<Check> checksOfField = checksByField.get(field);

		if (checksOfField == null) return;

		checksOfField.remove(check);
		if (checksOfField.size() == 0)
		{
			checksByField.remove(field);
			constrainedFields.remove(field);
		}
	}

	synchronized void removeCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstraintAnnotationNotPresentException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		HashMap<Integer, HashSet<Check>> checksOfMethodByParameter = checksByMethodParameter
				.get(method);
		if (checksOfMethodByParameter == null) return;

		{
			checksOfMethodByParameter = new HashMap<Integer, HashSet<Check>>();
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		HashSet<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null) return;

		checksOfMethodParameter.remove(check);

		if (checksOfMethodParameter.size() == 0)
		{
			checksOfMethodByParameter.remove(parameterIndex);
			if (checksOfMethodByParameter.size() == 0)
				constrainedParameterizedMethods.remove(method);
		}
	}

	private void setupChecksByConstructorParameters() throws ReflectionException, SecurityException
	{
		// loop over all constructors
		for (final Constructor constructor : clazz.getDeclaredConstructors())
		{
			final HashMap<Integer, HashSet<Check>> checksByConstructorParam = new HashMap<Integer, HashSet<Check>>();
			final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

			// loop over all parameters of the current constructor
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final HashSet<Check> parameterChecks = new HashSet<Check>();

				// loop over all annotations of the current parameter
				for (final Annotation annotation : parameterAnnotations[i])
				{
					// check if the current annotation is a constraint annotation
					if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					{
						if (constrainedAnnotation == null)
							throw new ConstraintAnnotationNotPresentException(
									"Cannot apply constructor parameter constraints to class "
											+ clazz.getName() + ".  @"
											+ Constrained.class.getName()
											+ " annotation not present.", new ClassContext(clazz));
						parameterChecks.add(loadCheck(annotation));
					}
				}
				if (parameterChecks.size() >= 0) checksByConstructorParam.put(i, parameterChecks);
			}
			if (checksByConstructorParam.size() > 0)
			{
				checksByConstructorParameter.put(constructor, checksByConstructorParam);
				constrainedParameterizedConstructors.add(constructor);
			}
		}
	}

	private void setupChecksByField() throws ReflectionException, SecurityException
	{
		// loop over all fields
		for (final Field field : clazz.getDeclaredFields())
		{
			final HashSet<Check> fieldChecks = new HashSet<Check>();

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					fieldChecks.add(loadCheck(annotation));
				}
			}
			if (fieldChecks.size() > 0)
			{
				constrainedFields.add(field);
				checksByField.put(field, fieldChecks);
			}
		}
	}

	private void setupChecksByMethod() throws ReflectionException, SecurityException
	{
		// loop over all methods
		for (final Method method : clazz.getDeclaredMethods())
		{
			// ensure the method has a return type
			if (method.getReturnType() == Void.TYPE)
			{
				if (LOG.isLoggable(Level.FINE))
					LOG
							.fine("Constraints for methods "
									+ method
									+ " will be ignored, because methods without return type are not supported.");
				continue;
			}

			final boolean isGetter = isGetter(method);

			if (!isGetter && constrainedAnnotation == null)
			{
				if (LOG.isLoggable(Level.FINE))
					LOG
							.fine("Return value constraints for parameterized method "
									+ method
									+ " will be ignored, because class is not annotated with @Constrained.");
				continue;
			}

			final HashSet<Check> returnValueChecks = new HashSet<Check>();

			// loop over all annotations
			for (final Annotation annotation : method.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					returnValueChecks.add(loadCheck(annotation));
				}
			}
			if (returnValueChecks.size() > 0)
			{
				if (isGetter)
				{
					constrainedGetters.add(method);
					checksByGetter.put(method, returnValueChecks);
				}
				constrainedMethods.add(method);
				checksByMethod.put(method, returnValueChecks);
			}
		}
	}

	private void setupChecksByMethodParameters() throws ReflectionException, SecurityException
	{
		// loop over all methods
		for (final Method method : clazz.getDeclaredMethods())
		{
			final HashMap<Integer, HashSet<Check>> checksByMethodParam = new HashMap<Integer, HashSet<Check>>();
			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

			// loop over all parameters of the current method
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final HashSet<Check> parameterChecks = new HashSet<Check>();

				// loop over all annotations of the current parameter
				for (final Annotation annotation : parameterAnnotations[i])
				{
					// check if the current annotation is a constraint annotation
					if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					{
						if (constrainedAnnotation == null)
							throw new ConstraintAnnotationNotPresentException(
									"Cannot apply method parameter constraints to class "
											+ clazz.getName() + ".  @"
											+ Constrained.class.getName()
											+ " annotation not present.", new ClassContext(clazz));
						parameterChecks.add(loadCheck(annotation));
					}
				}
				if (parameterChecks.size() >= 0) checksByMethodParam.put(i, parameterChecks);
			}
			if (checksByMethodParam.size() > 0)
			{
				checksByMethodParameter.put(method, checksByMethodParam);
				constrainedParameterizedMethods.add(method);
			}

			/*
			 * applying field constraints to the parameter of setter methods 
			 */

			// check if field constraints need to be applied to the parameter of setter methods
			if (constrainedAnnotation == null
					|| !constrainedAnnotation.applyFieldConstraintsToSetter()) continue;

			// check if method is a setter
			if (!isSetter(method)) continue;

			final Class< ? >[] methodParameterTypes = method.getParameterTypes();

			final String methodName = method.getName();
			final int methodNameLen = methodName.length();

			// check if a field with name xXX exists
			String fieldName = methodName.substring(3, 4).toLowerCase();
			if (methodNameLen > 4)
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

			// if method parameter type is boolean then check if a field with name isXXX exists
			if (field == null
					&& (methodParameterTypes[0].equals(boolean.class) || methodParameterTypes[0]
							.equals(Boolean.class)))
			{
				fieldName = "is" + methodName.substring(3);

				try
				{
					field = clazz.getDeclaredField(fieldName);

					// check if found field is of boolean or Boolean
					if (!field.getType().equals(boolean.class)
							&& field.getType().equals(Boolean.class))
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

			// check if a corresponding field has been found
			if (field != null)
			{
				FieldConstraintsCheck check = new FieldConstraintsCheck();
				check.setFieldName(field.getName());
				checksByMethodParam.get(0).add(check);

				/*
				 final HashSet<Check> checks = checksByField.get(field);
				 if (checks != null)
				 {
				 if (LOG.isLoggable(Level.FINE))
				 LOG.fine("Applying " + checks.size() + " constraint(s) defined for field <"
				 + fieldName + "> to setter <" + methodName + "> of class "
				 + clazz.getName());
				 checksByMethodParam.get(0).addAll(checks);
				 }*/
			}
		}
	}

	private boolean isGetter(Method method)
	{
		return (method.getParameterTypes().length == 0)
				&& (method.getName().startsWith("is") || method.getName().startsWith("get"));
	}

	private boolean isSetter(Method method)
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
}
