/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstructorConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodPostExecutionConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodPreExecutionConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.configuration.pojo.elements.ObjectConfiguration;
import net.sf.oval.configuration.pojo.elements.ParameterConfiguration;
import net.sf.oval.exception.OValException;
import net.sf.oval.exception.ReflectionException;
import net.sf.oval.guard.Guarded;
import net.sf.oval.guard.Post;
import net.sf.oval.guard.PostCheck;
import net.sf.oval.guard.PostValidateThis;
import net.sf.oval.guard.Pre;
import net.sf.oval.guard.PreCheck;
import net.sf.oval.guard.PreValidateThis;
import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class AnnotationsConfigurer implements Configurer
{
	protected void configureConstructorParameterChecks(final ClassConfiguration config)
	{
		for (final Constructor< ? > constructor : config.type.getDeclaredConstructors())
		{
			final List<ParameterConfiguration> parametersConfig = CollectionFactoryHolder
					.getFactory().createList(4);

			final Annotation[][] parameterAnnotations = constructor.getParameterAnnotations();

			// loop over all parameters of the current constructor
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final List<Check> parameterChecks = CollectionFactoryHolder.getFactory()
						.createList(4);

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
			final boolean postValidateThis = constructor
					.isAnnotationPresent(PostValidateThis.class);

			if (parametersConfig.size() > 0 | postValidateThis)
			{
				if (config.constructorConfigurations == null)
					config.constructorConfigurations = CollectionFactoryHolder.getFactory()
							.createSet(2);

				final ConstructorConfiguration cc = new ConstructorConfiguration();
				cc.parameterConfigurations = parametersConfig;
				cc.postCheckInvariants = postValidateThis;
				config.constructorConfigurations.add(cc);
			}
		}
	}

	protected void configureFieldChecks(final ClassConfiguration config)
	{
		for (final Field field : config.type.getDeclaredFields())
		{
			final List<Check> checks = CollectionFactoryHolder.getFactory().createList(2);

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
			{
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					checks.add(initializeCheck(annotation));
				}
			}
			if (checks.size() > 0)
			{
				if (config.fieldConfigurations == null)
					config.fieldConfigurations = CollectionFactoryHolder.getFactory().createSet(4);

				final FieldConfiguration fc = new FieldConfiguration();
				fc.name = field.getName();
				fc.checks = checks;
				config.fieldConfigurations.add(fc);
			}
		}
	}

	/**
	 * configure method return value and parameter checks
	 */
	protected void configureMethodChecks(final ClassConfiguration config)
	{
		for (final Method method : config.type.getDeclaredMethods())
		{
			/*
			 * determine method return value checks
			 */
			final List<Check> returnValueChecks = CollectionFactoryHolder.getFactory()
					.createList(2);
			final List<PreCheck> preChecks = CollectionFactoryHolder.getFactory().createList(2);
			final List<PostCheck> postChecks = CollectionFactoryHolder.getFactory().createList(2);
			boolean preValidateThis = false;
			boolean postValidateThis = false;

			// loop over all annotations
			for (final Annotation annotation : method.getAnnotations())
			{
				if (annotation instanceof Pre)
				{
					final PreCheck pc = new PreCheck();
					pc.configure((Pre) annotation);
					preChecks.add(pc);
				}
				else if (annotation instanceof PreValidateThis)
				{
					preValidateThis = true;
				}
				else if (annotation instanceof Post)
				{
					final PostCheck pc = new PostCheck();
					pc.configure((Post) annotation);
					postChecks.add(pc);
				}
				else if (annotation instanceof PostValidateThis)
				{
					postValidateThis = true;
				}

				// check if the current annotation is a constraint annotation
				else if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				{
					returnValueChecks.add(initializeCheck(annotation));
				}
			}

			/*
			 * determine parameter checks
			 */
			final List<ParameterConfiguration> parametersConfig = CollectionFactoryHolder
					.getFactory().createList(4);

			final Annotation[][] parameterAnnotations = method.getParameterAnnotations();

			// loop over all parameters of the current method
			for (int i = 0; i < parameterAnnotations.length; i++)
			{
				final List<Check> parameterChecks = CollectionFactoryHolder.getFactory()
						.createList(4);

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

			if (parametersConfig.size() > 0 || returnValueChecks.size() > 0 || preChecks.size() > 0
					|| postChecks.size() > 0 || preValidateThis || postValidateThis)
			{
				if (config.methodConfigurations == null)
					config.methodConfigurations = CollectionFactoryHolder.getFactory().createSet(4);

				final MethodConfiguration mc = new MethodConfiguration();
				mc.name = method.getName();
				mc.parameterConfigurations = parametersConfig;
				mc.isInvariant = method.isAnnotationPresent(IsInvariant.class);
				mc.preCheckInvariants = preValidateThis;
				mc.postCheckInvariants = postValidateThis;
				if (returnValueChecks.size() > 0)
				{
					mc.returnValueConfiguration = new MethodReturnValueConfiguration();
					mc.returnValueConfiguration.checks = returnValueChecks;
				}
				if (preChecks.size() > 0)
				{
					mc.preExecutionConfiguration = new MethodPreExecutionConfiguration();
					mc.preExecutionConfiguration.checks = preChecks;
				}
				if (postChecks.size() > 0)
				{
					mc.postExecutionConfiguration = new MethodPostExecutionConfiguration();
					mc.postExecutionConfiguration.checks = postChecks;
				}
				config.methodConfigurations.add(mc);
			}
		}
	}

	protected void configureObjectLevelChecks(final ClassConfiguration config)
	{
		final List<Check> checks = CollectionFactoryHolder.getFactory().createList(2);
		for (final Annotation annotation : config.type.getAnnotations())
		{
			// check if the current annotation is a constraint annotation
			if (annotation.annotationType().isAnnotationPresent(Constraint.class))
			{
				checks.add(initializeCheck(annotation));
			}
		}
		if (checks.size() > 0)
		{
			config.objectConfiguration = new ObjectConfiguration();
			config.objectConfiguration.checks = checks;
		}
	}

	public ClassConfiguration getClassConfiguration(final Class< ? > clazz) throws OValException
	{
		final ClassConfiguration config = new ClassConfiguration();
		config.type = clazz;

		config.applyFieldConstraintsToConstructors = config.type.isAnnotationPresent(Guarded.class)
				? config.type.getAnnotation(Guarded.class).applyFieldConstraintsToConstructors()
				: false;

		config.applyFieldConstraintsToSetters = config.type.isAnnotationPresent(Guarded.class)
				? config.type.getAnnotation(Guarded.class).applyFieldConstraintsToSetters() : false;

		config.checkInvariants = config.type.isAnnotationPresent(Guarded.class) ? config.type
				.getAnnotation(Guarded.class).checkInvariants() : false;

		configureObjectLevelChecks(config);

		configureFieldChecks(config);

		configureConstructorParameterChecks(config);

		configureMethodChecks(config);

		return config;
	}

	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
			throws OValException
	{
		return null;
	}

	protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> initializeCheck(
			final ConstraintAnnotation constraintAnnotation) throws ReflectionException
	{
		assert constraintAnnotation != null;

		final Constraint constraint = constraintAnnotation.annotationType().getAnnotation(
				Constraint.class);

		// determine the check class
		final Class< ? > checkClass = constraint.checkWith();

		try
		{
			// instantiate the appropriate check for the found constraint
			@SuppressWarnings("unchecked")
			final AnnotationCheck<ConstraintAnnotation> check = (AnnotationCheck<ConstraintAnnotation>) checkClass
					.newInstance();
			check.configure(constraintAnnotation);
			return check;
		}
		catch (final Exception e)
		{
			throw new ReflectionException("Cannot initialize constraint check "
					+ checkClass.getName(), e);
		}
	}
}
