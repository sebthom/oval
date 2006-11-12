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
package net.sf.oval.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.oval.AnnotationCheck;
import net.sf.oval.Check;
import net.sf.oval.annotations.Constrained;
import net.sf.oval.annotations.Constraint;
import net.sf.oval.annotations.DefineConstraintSet;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.ConstructorConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.configuration.elements.MethodConfiguration;
import net.sf.oval.configuration.elements.ParameterConfiguration;
import net.sf.oval.constraints.AssertFieldConstraintsCheck;
import net.sf.oval.exceptions.OValException;
import net.sf.oval.exceptions.ReflectionException;
import net.sf.oval.utils.ReflectionUtils;

/**
 * @author Sebastian Thomschke
 */
public class AnnotationsConfigurer implements Configurer
{
	public ClassConfiguration getClassConfiguration(Class< ? > clazz) throws OValException
	{
		final ClassConfiguration config = new ClassConfiguration();
		config.type = clazz;
		config.applyFieldConstraintsToSetter = config.type.isAnnotationPresent(Constrained.class)
				? config.type.getAnnotation(Constrained.class).applyFieldConstraintsToSetter()
				: false;

		/*
		 * determine field checks
		 */
		for (final Field field : config.type.getDeclaredFields())
		{
			final List<Check> checks = CollectionFactory.INSTANCE.createList(4);
			String definedConstraintSetId = null;

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					checks.add(initializeCheck(annotation));
				}

				// check if the current annotation is a constraintSet definition
				else if (annotation instanceof DefineConstraintSet)
				{
					definedConstraintSetId = ((DefineConstraintSet) annotation).value();
				}
			}
			if (checks.size() > 0)
			{
				if (config.fieldConfigurations == null)
					config.fieldConfigurations = CollectionFactory.INSTANCE.createSet(8);

				final FieldConfiguration fc = new FieldConfiguration();
				fc.name = field.getName();
				fc.checks = checks;
				fc.defineConstraintSet = definedConstraintSetId;
				config.fieldConfigurations.add(fc);
			}
		}

		/*
		 * determine constructor parameter checks
		 */
		for (final Constructor constructor : config.type.getDeclaredConstructors())
		{
			final List<ParameterConfiguration> parametersConfig = CollectionFactory.INSTANCE
					.createList(4);

			final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

			// loop over all parameters of the current constructor
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final List<Check> parameterChecks = CollectionFactory.INSTANCE.createList(4);

				// loop over all annotations of the current constructor parameter
				for (final Annotation annotation : parameterAnnotations[i])
				{
					// check if the current annotation is a constraint annotation
					if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					{
						parameterChecks.add(initializeCheck(annotation));
					}
				}

				final ParameterConfiguration pc = new ParameterConfiguration();
				parametersConfig.add(pc);
				pc.type = constructor.getParameterTypes()[i];
				pc.checks = parameterChecks;
			}
			if (parametersConfig.size() > 0)
			{
				if (config.constructorConfigurations == null)
					config.constructorConfigurations = CollectionFactory.INSTANCE.createSet(2);

				final ConstructorConfiguration cc = new ConstructorConfiguration();
				cc.parameterConfigurations = parametersConfig;
				config.constructorConfigurations.add(cc);
			}
		}

		/*
		 * determine method return value and parameter checks
		 */
		for (final Method method : config.type.getDeclaredMethods())
		{
			/*
			 * determine method return value checks
			 */
			final List<Check> returnValueChecks = CollectionFactory.INSTANCE.createList(4);

			// loop over all annotations
			for (final Annotation annotation : method.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					returnValueChecks.add(initializeCheck(annotation));
				}
			}

			/*
			 * determine parameter checks
			 */
			final List<ParameterConfiguration> parametersConfig = CollectionFactory.INSTANCE
					.createList(4);

			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

			// loop over all parameters of the current method
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final List<Check> parameterChecks = CollectionFactory.INSTANCE.createList(4);

				// loop over all annotations of the current method parameter
				for (final Annotation annotation : parameterAnnotations[i])
				{
					// check if the current annotation is a constraint annotation
					if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					{
						parameterChecks.add(initializeCheck(annotation));
					}
				}

				final ParameterConfiguration pc = new ParameterConfiguration();
				parametersConfig.add(pc);
				pc.type = method.getParameterTypes()[i];
				pc.checks = parameterChecks;
			}

			/* *******************
			 * applying field constraints to the single parameter of setter methods 
			 * *******************/
			if (config.applyFieldConstraintsToSetter)
			{
				final Field field = ReflectionUtils.getFieldForSetter(method);

				// check if a corresponding field has been found
				if (field != null)
				{
					final AssertFieldConstraintsCheck check = new AssertFieldConstraintsCheck();
					check.setFieldName(field.getName());
					parametersConfig.get(0).checks.add(check);
				}
			}

			if (parametersConfig.size() > 0 || returnValueChecks.size() > 0)
			{
				if (config.methodConfigurations == null)
					config.methodConfigurations = CollectionFactory.INSTANCE.createSet(8);

				final MethodConfiguration mc = new MethodConfiguration();
				mc.name = method.getName();
				mc.parameterConfigurations = parametersConfig;
				mc.returnValueChecks = returnValueChecks;
				config.methodConfigurations.add(mc);
			}
		}
		return config;
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

	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
			throws OValException
	{
		return null;
	}
}
