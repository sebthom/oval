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
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.Constraint;
import net.sf.oval.annotations.DefineConstraintSet;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.constraints.AssertFieldConstraintsCheck;
import net.sf.oval.contexts.ClassContext;
import net.sf.oval.contexts.FieldContext;
import net.sf.oval.exceptions.ConstrainedAnnotationNotPresentException;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.utils.ReflectionUtils;

/**
 * This class holds information about all the constraints defined for a class
 * 
 * @author Sebastian Thomschke
 * @version $Revision: 1.10 $
 */
final class ClassConfiguration
{
	private static final Logger LOG = Logger.getLogger(ClassConfiguration.class.getName());

	final Map<String, ConstraintSet> constraintSetsByShortId = CollectionFactory.INSTANCE
			.createMap();

	/**
	 * checks on constructors' parameter values
	 */
	final Map<Constructor, Map<Integer, Set<Check>>> checksByConstructorParameter = CollectionFactory.INSTANCE
			.createMap(4);

	/**
	 * checks on fields' value
	 */
	final Map<Field, Set<Check>> checksByField = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on getter methods' return value
	 */
	final Map<Method, Set<Check>> checksByGetter = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on parameterized methods' return value
	 */
	final Map<Method, Set<Check>> checksByMethod = CollectionFactory.INSTANCE.createMap();

	/**
	 * checks on methods' parameter values
	 */
	final Map<Method, Map<Integer, Set<Check>>> checksByMethodParameter = CollectionFactory.INSTANCE
			.createMap();

	final Class clazz;

	final Constrained constrainedAnnotation;
	final Set<Field> constrainedFields = CollectionFactory.INSTANCE.createSet();
	final Set<Method> constrainedGetters = CollectionFactory.INSTANCE.createSet();

	final Set<Method> constrainedMethods = CollectionFactory.INSTANCE.createSet();

	final Set<Constructor> constrainedParameterizedConstructors = CollectionFactory.INSTANCE
			.createSet(4);

	final Set<Method> constrainedParameterizedMethods = CollectionFactory.INSTANCE.createSet();

	final Validator validator;

	/**
	 * package constructor used by the Validator class
	 * 
	 * @param clazz
	 * @param validator
	 * @throws ReflectionException
	 */
	@SuppressWarnings("unchecked")
	ClassConfiguration(final Class clazz, final Validator validator) throws ReflectionException
	{
		if (LOG.isLoggable(Level.FINE))
			LOG.fine("Initializing constraints configuration for class " + clazz);

		this.clazz = clazz;
		this.validator = validator;

		constrainedAnnotation = (Constrained) clazz.getAnnotation(Constrained.class);

		if (constrainedAnnotation == null)
		{
			if (LOG.isLoggable(Level.FINE))
			{
				LOG.log(Level.FINE, clazz.getName() + ": @" + Constrained.class.getName()
						+ " annotation not present.", new ConstrainedAnnotationNotPresentException(
						"@" + Constrained.class.getName() + " annotation not present.",
						new ClassContext(clazz)));
			}
		}

		try
		{
			setupChecksByField(); // field checks must be initialized first!
			setupChecksByConstructorParameters();
			setupChecksByMethodParameters();
			setupChecksByMethod();
		}
		catch (final SecurityException e)
		{
			throw new ReflectionException("Cannot initialize constraints configuration for class "
					+ clazz.getName(), e);
		}
	}

	/**
	 * adds a constraint to a constructor parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	synchronized void addCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws ConstrainedAnnotationNotPresentException
	{
		// check of the @Constrained class level annotation is present
		if (constrainedAnnotation == null)
			throw new ConstrainedAnnotationNotPresentException(
					"Cannot apply constructor parameter constraints to class " + clazz.getName()
							+ ".  @" + Constrained.class.getName() + " annotation not present.",
					new ClassContext(clazz));

		// retrieve the currently registered checks for all parameters of the specified constructor
		Map<Integer, Set<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null)
		{
			checksOfConstructorByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		Set<Check> checksOfConstructorParameter = checksOfConstructorByParameter
				.get(parameterIndex);
		if (checksOfConstructorParameter == null)
		{
			checksOfConstructorParameter = CollectionFactory.INSTANCE.createSet(8);
			checksOfConstructorByParameter.put(parameterIndex, checksOfConstructorParameter);
		}

		checksOfConstructorParameter.add(check);
	}

	/**
	 * adds a constraint to a field 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	synchronized void addCheck(final Field field, final Check check)
	{
		Set<Check> checksOfField = checksByField.get(field);
		if (checksOfField == null)
		{
			checksOfField = CollectionFactory.INSTANCE.createSet(8);
			checksByField.put(field, checksOfField);
			constrainedFields.add(field);
		}
		checksOfField.add(check);
	}

	/**
	 * adds a constraint to a method parameter 
	 *  
	 * @param constructor
	 * @param parameterIndex
	 * @param check
	 * @throws ConstrainedAnnotationNotPresentException
	 */
	synchronized void addCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstrainedAnnotationNotPresentException
	{
		// check of the @Constrained class level annotation is present 
		if (constrainedAnnotation == null)
			throw new ConstrainedAnnotationNotPresentException(
					"Cannot apply method parameter constraints to class " + clazz.getName()
							+ ".  @" + Constrained.class.getName() + " annotation not present.",
					new ClassContext(clazz));

		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Set<Check>> checksOfMethodByParameter = checksByMethodParameter.get(method);
		if (checksOfMethodByParameter == null)
		{
			checksOfMethodByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		Set<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
		if (checksOfMethodParameter == null)
		{
			checksOfMethodParameter = CollectionFactory.INSTANCE.createSet(8);
			checksOfMethodByParameter.put(parameterIndex, checksOfMethodParameter);
		}

		checksOfMethodParameter.add(check);
	}

	private <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> initializeCheck(
			final ConstraintAnnotation constraintAnnotation) throws ReflectionException
	{
		final Constraint constraint = constraintAnnotation.annotationType().getAnnotation(
				Constraint.class);
		final Class checkClass = constraint.check();

		try
		{
			// instantiate the appropriate check for the found constraint
			@SuppressWarnings("unchecked")
			final AnnotationCheck<ConstraintAnnotation> check = (AnnotationCheck<ConstraintAnnotation>) checkClass
					.newInstance();
			check.configure(constraintAnnotation);
			return check;
		}
		catch (Exception e)
		{
			throw new ReflectionException("Cannot initialize constraint check "
					+ checkClass.getName(), e);
		}
	}

	synchronized void removeCheck(final Constructor constructor, final int parameterIndex,
			final Check check) throws ConstrainedAnnotationNotPresentException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Set<Check>> checksOfConstructorByParameter = checksByConstructorParameter
				.get(constructor);
		if (checksOfConstructorByParameter == null) return;

		{
			checksOfConstructorByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByConstructorParameter.put(constructor, checksOfConstructorByParameter);
			constrainedParameterizedConstructors.add(constructor);
		}

		// retrieve the checks for the specified parameter
		final Set<Check> checksOfConstructorParameter = checksOfConstructorByParameter
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
		final Set<Check> checksOfField = checksByField.get(field);

		if (checksOfField == null) return;

		checksOfField.remove(check);
		if (checksOfField.size() == 0)
		{
			checksByField.remove(field);
			constrainedFields.remove(field);
		}
	}

	synchronized void removeCheck(final Method method, final int parameterIndex, final Check check)
			throws ConstrainedAnnotationNotPresentException
	{
		// retrieve the currently registered checks for all parameters of the specified method
		Map<Integer, Set<Check>> checksOfMethodByParameter = checksByMethodParameter.get(method);
		if (checksOfMethodByParameter == null) return;

		{
			checksOfMethodByParameter = CollectionFactory.INSTANCE.createMap(8);
			checksByMethodParameter.put(method, checksOfMethodByParameter);
			constrainedParameterizedMethods.add(method);
		}

		// retrieve the checks for the specified parameter
		Set<Check> checksOfMethodParameter = checksOfMethodByParameter.get(parameterIndex);
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
			final Map<Integer, Set<Check>> checksByConstructorParam = CollectionFactory.INSTANCE
					.createMap(8);
			final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

			// loop over all parameters of the current constructor
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final Set<Check> parameterChecks = CollectionFactory.INSTANCE.createSet(8);

				// loop over all annotations of the current constructor parameter
				for (final Annotation annotation : parameterAnnotations[i])
				{
					// check if the current annotation is a constraint annotation
					if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					{
						// if an constraint annotation was specified but the class is not annotated with @Constrained throw an exception
						if (constrainedAnnotation == null)
							throw new ConstrainedAnnotationNotPresentException(
									"Cannot apply constructor parameter constraints to class "
											+ clazz.getName() + ". @" + Constrained.class.getName()
											+ " annotation not present.", new ClassContext(clazz));
						parameterChecks.add(initializeCheck(annotation));
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
			final Set<Check> fieldChecks = CollectionFactory.INSTANCE.createSet(8);

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					fieldChecks.add(initializeCheck(annotation));
				}

				// check if the current annotation is a constraintSet definition
				else if (annotation instanceof DefineConstraintSet)
				{
					AnnotationConstraintSet cs = new AnnotationConstraintSet();
					cs.shortId = ((DefineConstraintSet) annotation).value();
					cs.id = clazz.getName() + "." + cs.shortId;
					cs.context = new FieldContext(field);

					if (constraintSetsByShortId.containsKey(cs.shortId))
					{
						LOG.warning("Another constraint set with the same id " + cs.shortId
								+ "has already been defined in class " + clazz.getName() + ".");
						//TODO what to do?
					}
					constraintSetsByShortId.put(cs.shortId, cs);
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

			final boolean isGetter = ReflectionUtils.isGetter(method);

			if (!isGetter && constrainedAnnotation == null)
			{
				if (LOG.isLoggable(Level.FINE))
					LOG
							.fine("Return value constraints for parameterized method "
									+ method
									+ " will be ignored, because class is not annotated with @Constrained.");
				continue;
			}

			final Set<Check> returnValueChecks = CollectionFactory.INSTANCE.createSet(8);

			// loop over all annotations
			for (final Annotation annotation : method.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					returnValueChecks.add(initializeCheck(annotation));
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
			final Map<Integer, Set<Check>> checksByMethodParam = CollectionFactory.INSTANCE
					.createMap(8);
			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

			// loop over all parameters of the current method
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final Set<Check> parameterChecks = CollectionFactory.INSTANCE.createSet(8);

				// loop over all annotations of the current method parameter
				for (final Annotation annotation : parameterAnnotations[i])
				{
					// check if the current annotation is a constraint annotation
					if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					{
						// if an constraint annotation was specified but the class is not annotated with @Constrained throw an exception
						if (constrainedAnnotation == null)
							throw new ConstrainedAnnotationNotPresentException(
									"Cannot apply method parameter constraints to class "
											+ clazz.getName() + ".  @"
											+ Constrained.class.getName()
											+ " annotation not present.", new ClassContext(clazz));
						parameterChecks.add(initializeCheck(annotation));
					}
				}
				if (parameterChecks.size() >= 0) checksByMethodParam.put(i, parameterChecks);
			}
			if (checksByMethodParam.size() > 0)
			{
				checksByMethodParameter.put(method, checksByMethodParam);
				constrainedParameterizedMethods.add(method);
			}

			/* *******************
			 * applying field constraints to the single parameter of setter methods 
			 * *******************/

			// check if constraints specified for fields need to be applied to the single parameter of the corresponding setter methods
			if (constrainedAnnotation == null
					|| !constrainedAnnotation.applyFieldConstraintsToSetter()) continue;

			final Field field = ReflectionUtils.getFieldForSetter(method);

			// check if a corresponding field has been found
			if (field != null)
			{
				final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
				check.setFieldName(field.getName());
				checksByMethodParam.get(0).add(check);

				/*
				 final Set<Check> checks = checksByField.get(field);
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
}
