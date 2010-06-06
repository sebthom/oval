/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import static net.sf.oval.Validator.getCollectionFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.CheckExclusion;
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

/**
 * Configurer that configures constraints based on annotations tagged with {@link Constraint}
 * 
 * @author Sebastian Thomschke
 */
public class AnnotationsConfigurer implements Configurer
{
	private List<ParameterConfiguration> _createParameterConfiguration(final Annotation[][] paramAnnotations,
			final Class< ? >[] parameterTypes)
	{
		final List<ParameterConfiguration> paramCfg = getCollectionFactory().createList(2);

		// loop over all parameters of the current constructor
		for (int i = 0; i < paramAnnotations.length; i++)
		{
			final List<Check> paramChecks = getCollectionFactory().createList(2);
			final List<CheckExclusion> paramCheckExclusions = getCollectionFactory().createList(2);

			// loop over all annotations of the current constructor parameter
			for (final Annotation annotation : paramAnnotations[i])
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					paramChecks.add(initializeCheck(annotation));
				else if (annotation.annotationType().isAnnotationPresent(Constraints.class))
					initializeChecks(annotation, paramChecks);
				else if (annotation.annotationType().isAnnotationPresent(Exclusion.class))
					paramCheckExclusions.add(initializeExclusion(annotation));

			final ParameterConfiguration pc = new ParameterConfiguration();
			paramCfg.add(pc);
			pc.type = parameterTypes[i];
			pc.checks = paramChecks.size() == 0 ? null : paramChecks;
			pc.checkExclusions = paramCheckExclusions.size() == 0 ? null : paramCheckExclusions;
		}
		return paramCfg;
	}

	protected void configureConstructorParameterChecks(final ClassConfiguration classCfg)
	{
		for (final Constructor< ? > ctor : classCfg.type.getDeclaredConstructors())
		{
			final List<ParameterConfiguration> paramCfg = _createParameterConfiguration(ctor.getParameterAnnotations(),
					ctor.getParameterTypes());

			final boolean postValidateThis = ctor.isAnnotationPresent(PostValidateThis.class);

			if (paramCfg.size() > 0 | postValidateThis)
			{
				if (classCfg.constructorConfigurations == null)
					classCfg.constructorConfigurations = getCollectionFactory().createSet(2);

				final ConstructorConfiguration cc = new ConstructorConfiguration();
				cc.parameterConfigurations = paramCfg;
				cc.postCheckInvariants = postValidateThis;
				classCfg.constructorConfigurations.add(cc);
			}
		}
	}

	protected void configureFieldChecks(final ClassConfiguration classCfg)
	{
		for (final Field field : classCfg.type.getDeclaredFields())
		{
			final List<Check> checks = getCollectionFactory().createList(2);

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
				// check if the current annotation is a constraint annotation
				if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					checks.add(initializeCheck(annotation));
				else if (annotation.annotationType().isAnnotationPresent(Constraints.class))
					initializeChecks(annotation, checks);
			if (checks.size() > 0)
			{
				if (classCfg.fieldConfigurations == null)
					classCfg.fieldConfigurations = getCollectionFactory().createSet(2);

				final FieldConfiguration fc = new FieldConfiguration();
				fc.name = field.getName();
				fc.checks = checks;
				classCfg.fieldConfigurations.add(fc);
			}
		}
	}

	/**
	 * configure method return value and parameter checks
	 */
	protected void configureMethodChecks(final ClassConfiguration classCfg)
	{
		for (final Method method : classCfg.type.getDeclaredMethods())
		{
			/*
			 * determine method return value checks and method pre/post
			 * conditions
			 */
			final List<Check> returnValueChecks = getCollectionFactory().createList(2);
			final List<PreCheck> preChecks = getCollectionFactory().createList(2);
			final List<PostCheck> postChecks = getCollectionFactory().createList(2);
			boolean preValidateThis = false;
			boolean postValidateThis = false;

			// loop over all annotations
			for (final Annotation annotation : method.getAnnotations())
				if (annotation instanceof Pre)
				{
					final PreCheck pc = new PreCheck();
					pc.configure((Pre) annotation);
					preChecks.add(pc);
				}
				else if (annotation instanceof PreValidateThis)
					preValidateThis = true;
				else if (annotation instanceof Post)
				{
					final PostCheck pc = new PostCheck();
					pc.configure((Post) annotation);
					postChecks.add(pc);
				}
				else if (annotation instanceof PostValidateThis)
					postValidateThis = true;
				else if (annotation.annotationType().isAnnotationPresent(Constraint.class))
					returnValueChecks.add(initializeCheck(annotation));
				else if (annotation.annotationType().isAnnotationPresent(Constraints.class))
					initializeChecks(annotation, returnValueChecks);

			/*
			 * determine parameter checks
			 */
			final List<ParameterConfiguration> paramCfg = _createParameterConfiguration(method
					.getParameterAnnotations(), method.getParameterTypes());

			// check if anything has been configured for this method at all
			if (paramCfg.size() > 0 || returnValueChecks.size() > 0 || preChecks.size() > 0 || postChecks.size() > 0
					|| preValidateThis || postValidateThis)
			{
				if (classCfg.methodConfigurations == null)
					classCfg.methodConfigurations = getCollectionFactory().createSet(2);

				final MethodConfiguration mc = new MethodConfiguration();
				mc.name = method.getName();
				mc.parameterConfigurations = paramCfg;
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
				classCfg.methodConfigurations.add(mc);
			}
		}
	}

	protected void configureObjectLevelChecks(final ClassConfiguration classCfg)
	{
		final List<Check> checks = getCollectionFactory().createList(2);
		for (final Annotation annotation : classCfg.type.getAnnotations())
			// check if the current annotation is a constraint annotation
			if (annotation.annotationType().isAnnotationPresent(Constraint.class))
				checks.add(initializeCheck(annotation));
			else if (annotation.annotationType().isAnnotationPresent(Constraints.class))
				initializeChecks(annotation, checks);
		if (checks.size() > 0)
		{
			classCfg.objectConfiguration = new ObjectConfiguration();
			classCfg.objectConfiguration.checks = checks;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public ClassConfiguration getClassConfiguration(final Class< ? > clazz)
	{
		final ClassConfiguration classCfg = new ClassConfiguration();
		classCfg.type = clazz;

		final Guarded guarded = clazz.getAnnotation(Guarded.class);

		classCfg.applyFieldConstraintsToConstructors = guarded != null && guarded.applyFieldConstraintsToConstructors();
		classCfg.applyFieldConstraintsToSetters = guarded != null && guarded.applyFieldConstraintsToSetters();
		classCfg.assertParametersNotNull = guarded != null && guarded.assertParametersNotNull();
		classCfg.checkInvariants = guarded != null && guarded.checkInvariants();

		configureObjectLevelChecks(classCfg);
		configureFieldChecks(classCfg);
		configureConstructorParameterChecks(classCfg);
		configureMethodChecks(classCfg);

		return classCfg;
	}

	/**
	 * {@inheritDoc}
	 */
	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
	{
		return null;
	}

	protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> initializeCheck(
			final ConstraintAnnotation constraintAnnotation) throws ReflectionException
	{
		assert constraintAnnotation != null;

		final Constraint constraint = constraintAnnotation.annotationType().getAnnotation(Constraint.class);

		// determine the check class
		@SuppressWarnings("unchecked")
		final Class<AnnotationCheck<ConstraintAnnotation>> checkClass = (Class<AnnotationCheck<ConstraintAnnotation>>) constraint
				.checkWith();

		// instantiate the appropriate check for the found constraint
		final AnnotationCheck<ConstraintAnnotation> check = newCheckInstance(checkClass);
		check.configure(constraintAnnotation);
		return check;
	}

	protected <ConstraintsAnnotation extends Annotation> void initializeChecks(
			final ConstraintsAnnotation constraintsAnnotation, final List<Check> checks) throws ReflectionException
	{
		try
		{
			final Method getValue = constraintsAnnotation.annotationType().getDeclaredMethod("value",
					(Class< ? >[]) null);
			final Object[] constraintAnnotations = (Object[]) getValue.invoke(constraintsAnnotation, (Object[]) null);
			for (final Object ca : constraintAnnotations)
				checks.add(initializeCheck((Annotation) ca));
		}
		catch (final ReflectionException ex)
		{
			throw ex;
		}
		catch (final Exception e)
		{
			throw new ReflectionException("Cannot initialize constraint check "
					+ constraintsAnnotation.annotationType().getName(), e);
		}
	}

	protected <ExclusionAnnotation extends Annotation> AnnotationCheckExclusion<ExclusionAnnotation> initializeExclusion(
			final ExclusionAnnotation exclusionAnnotation) throws ReflectionException
	{
		assert exclusionAnnotation != null;

		final Exclusion constraint = exclusionAnnotation.annotationType().getAnnotation(Exclusion.class);

		// determine the check class
		final Class< ? > exclusionClass = constraint.excludeWith();

		try
		{
			// instantiate the appropriate exclusion for the found annotation
			@SuppressWarnings("unchecked")
			final AnnotationCheckExclusion<ExclusionAnnotation> exclusion = (AnnotationCheckExclusion<ExclusionAnnotation>) exclusionClass
					.newInstance();
			exclusion.configure(exclusionAnnotation);
			return exclusion;
		}
		catch (final Exception e)
		{
			throw new ReflectionException("Cannot initialize constraint exclusion " + exclusionClass.getName(), e);
		}
	}

	/**
	 * @return a new instance of the given constraint check implementation class
	 */
	protected <ConstraintAnnotation extends Annotation> AnnotationCheck<ConstraintAnnotation> newCheckInstance(
			final Class<AnnotationCheck<ConstraintAnnotation>> checkClass) throws OValException
	{
		try
		{
			return checkClass.newInstance();
		}
		catch (final InstantiationException e)
		{
			throw new ReflectionException("Cannot initialize constraint check " + checkClass.getName(), e);
		}
		catch (final IllegalAccessException e)
		{
			throw new ReflectionException("Cannot initialize constraint check " + checkClass.getName(), e);
		}
	}
}
