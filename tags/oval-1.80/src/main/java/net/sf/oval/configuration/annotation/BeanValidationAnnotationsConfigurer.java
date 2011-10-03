/*******************************************************************************

 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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

import static net.sf.oval.Validator.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.AssertFalse;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.Future;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;

import net.sf.oval.Check;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodConfiguration;
import net.sf.oval.configuration.pojo.elements.MethodReturnValueConfiguration;
import net.sf.oval.constraint.AssertFalseCheck;
import net.sf.oval.constraint.AssertNullCheck;
import net.sf.oval.constraint.AssertTrueCheck;
import net.sf.oval.constraint.AssertValidCheck;
import net.sf.oval.constraint.DigitsCheck;
import net.sf.oval.constraint.FutureCheck;
import net.sf.oval.constraint.MatchPatternCheck;
import net.sf.oval.constraint.MaxCheck;
import net.sf.oval.constraint.MinCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.constraint.PastCheck;
import net.sf.oval.constraint.SizeCheck;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ReflectionUtils;

/**
 * Constraints configurer that interprets the JSR303 built-in Java Bean Validation annotations:
 * <ul>
 * <li>javax.validation.constraints.AssertFalse    => net.sf.oval.constraint.AssertFalseCheck
 * <li>javax.validation.constraints.AssertTrue     => net.sf.oval.constraint.AssertTrueCheck
 * <li>javax.validation.constraints.DecimalMax     => net.sf.oval.constraint.MaxCheck
 * <li>javax.validation.constraints.DecimalMin     => net.sf.oval.constraint.MinCheck
 * <li>javax.validation.constraints.Digits         => net.sf.oval.constraint.DigitsCheck
 * <li>javax.validation.constraints.Future         => net.sf.oval.constraint.FutureCheck
 * <li>javax.validation.constraints.Max            => net.sf.oval.constraint.MaxCheck
 * <li>javax.validation.constraints.Min            => net.sf.oval.constraint.MinCheck
 * <li>javax.validation.constraints.NotNull        => net.sf.oval.constraint.NotNullCheck
 * <li>javax.validation.constraints.Null           => net.sf.oval.constraint.AssertNullCheck
 * <li>javax.validation.constraints.Past           => net.sf.oval.constraint.PastCheck
 * <li>javax.validation.constraints.Pattern        => net.sf.oval.constraint.PatternCheck
 * <li>javax.validation.constraints.Size           => net.sf.oval.constraint.SizeCheck
 * <li>javax.validation.Valid                      => net.sf.oval.constraint.AssertValidCheck
 * </ul>
 * @author Sebastian Thomschke
 */
public class BeanValidationAnnotationsConfigurer implements Configurer
{
	private static final Log LOG = Log.getLog(BeanValidationAnnotationsConfigurer.class);

	protected Boolean applyFieldConstraintsToSetters;
	protected Boolean applyFieldConstraintsToConstructors;

	/**
	 * {@inheritDoc}
	 */
	public ClassConfiguration getClassConfiguration(final Class< ? > clazz)
	{
		final ClassConfiguration config = new ClassConfiguration();
		config.type = clazz;
		config.applyFieldConstraintsToConstructors = applyFieldConstraintsToConstructors;
		config.applyFieldConstraintsToSetters = applyFieldConstraintsToSetters;

		/*
		 * determine field checks
		 */
		for (final Field field : config.type.getDeclaredFields())
		{
			final List<Check> checks = getCollectionFactory().createList(4);

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
				initializeChecks(annotation, checks);

			if (checks.size() > 0)
			{
				if (config.fieldConfigurations == null)
					config.fieldConfigurations = getCollectionFactory().createSet(8);

				final FieldConfiguration fc = new FieldConfiguration();
				fc.name = field.getName();
				fc.checks = checks;
				config.fieldConfigurations.add(fc);
			}
		}

		/*
		 * determine getter checks
		 */
		for (final Method method : config.type.getDeclaredMethods())
		{
			// consider getters only 
			if (!ReflectionUtils.isGetter(method)) continue;

			final List<Check> checks = getCollectionFactory().createList(2);

			// loop over all annotations
			for (final Annotation annotation : method.getAnnotations())
				initializeChecks(annotation, checks);

			// check if anything has been configured for this method at all
			if (checks.size() > 0)
			{
				if (config.methodConfigurations == null)
					config.methodConfigurations = getCollectionFactory().createSet(2);

				final MethodConfiguration mc = new MethodConfiguration();
				mc.name = method.getName();
				mc.isInvariant = true;
				if (checks.size() > 0)
				{
					mc.returnValueConfiguration = new MethodReturnValueConfiguration();
					mc.returnValueConfiguration.checks = checks;
				}
				config.methodConfigurations.add(mc);
			}
		}
		return config;
	}

	/**
	 * {@inheritDoc}
	 */
	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
	{
		return null;
	}

	protected void initializeChecks(final Annotation annotation, final Collection<Check> checks)
	{
		assert annotation != null;
		assert checks != null;

		Class< ? >[] groups = null;
		Check check = null;
		if (annotation instanceof NotNull)
		{
			groups = ((NotNull) annotation).groups();
			check = new NotNullCheck();
		}
		else if (annotation instanceof Null)
		{
			groups = ((Null) annotation).groups();
			check = new AssertNullCheck();
		}
		else if (annotation instanceof Valid)
			check = new AssertValidCheck();
		else if (annotation instanceof AssertTrue)
		{
			groups = ((AssertTrue) annotation).groups();
			check = new AssertTrueCheck();
		}
		else if (annotation instanceof AssertFalse)
		{
			groups = ((AssertFalse) annotation).groups();
			check = new AssertFalseCheck();
		}
		else if (annotation instanceof DecimalMax)
		{
			groups = ((DecimalMax) annotation).groups();
			final MaxCheck maxCheck = new MaxCheck();
			maxCheck.setMax(Double.parseDouble(((DecimalMax) annotation).value()));
			check = maxCheck;
		}
		else if (annotation instanceof DecimalMin)
		{
			groups = ((DecimalMin) annotation).groups();
			final MinCheck minCheck = new MinCheck();
			minCheck.setMin(Double.parseDouble(((DecimalMin) annotation).value()));
			check = minCheck;
		}
		else if (annotation instanceof Max)
		{
			groups = ((Max) annotation).groups();
			final MaxCheck maxCheck = new MaxCheck();
			maxCheck.setMax(((Max) annotation).value());
			check = maxCheck;
		}
		else if (annotation instanceof Min)
		{
			groups = ((Min) annotation).groups();
			final MinCheck minCheck = new MinCheck();
			minCheck.setMin(((Min) annotation).value());
			check = minCheck;
		}
		else if (annotation instanceof Future)
		{
			groups = ((Future) annotation).groups();
			check = new FutureCheck();
		}
		else if (annotation instanceof Past)
		{
			groups = ((Past) annotation).groups();
			check = new PastCheck();
		}
		else if (annotation instanceof Pattern)
		{
			groups = ((Pattern) annotation).groups();
			final MatchPatternCheck matchPatternCheck = new MatchPatternCheck();
			int iflag = 0;
			for (final Flag flag : ((Pattern) annotation).flags())
				iflag = iflag | flag.getValue();
			matchPatternCheck.setPattern(((Pattern) annotation).regexp(), iflag);
			check = matchPatternCheck;
		}
		else if (annotation instanceof Digits)
		{
			groups = ((Digits) annotation).groups();
			final DigitsCheck digitsCheck = new DigitsCheck();
			digitsCheck.setMaxFraction(((Digits) annotation).fraction());
			digitsCheck.setMaxInteger(((Digits) annotation).integer());
			check = digitsCheck;
		}
		else if (annotation instanceof Size)
		{
			groups = ((Size) annotation).groups();
			final SizeCheck sizeCheck = new SizeCheck();
			sizeCheck.setMax(((Size) annotation).max());
			sizeCheck.setMin(((Size) annotation).min());
			check = sizeCheck;
		}

		if (check != null)
		{
			final Method getMessage = ReflectionUtils.getMethod(annotation.getClass(), "message", (Class< ? >[]) null);
			if (getMessage != null)
			{
				final String message = ReflectionUtils.invokeMethod(getMessage, annotation, (Object[]) null);
				if (message != null && !message.startsWith("javax.validation.constraints.")) check.setMessage(message);
			}

			if (groups != null && groups.length > 0)
			{
				final String[] profiles = new String[groups.length];
				for (int i = 0, l = groups.length; i < l; i++)
					profiles[i] = groups[i].getName();
				check.setProfiles(profiles);
			}
			checks.add(check);
			return;
		}

		Annotation[] list = null;
		if (annotation instanceof AssertFalse.List)
			list = ((AssertFalse.List) annotation).value();
		else if (annotation instanceof AssertTrue.List)
			list = ((AssertTrue.List) annotation).value();
		else if (annotation instanceof DecimalMax.List)
			list = ((DecimalMax.List) annotation).value();
		else if (annotation instanceof DecimalMin.List)
			list = ((DecimalMin.List) annotation).value();
		else if (annotation instanceof Digits.List)
			list = ((Digits.List) annotation).value();
		else if (annotation instanceof Future.List)
			list = ((Future.List) annotation).value();
		else if (annotation instanceof Max.List)
			list = ((Max.List) annotation).value();
		else if (annotation instanceof Min.List)
			list = ((Min.List) annotation).value();
		else if (annotation instanceof NotNull.List)
			list = ((NotNull.List) annotation).value();
		else if (annotation instanceof Null.List)
			list = ((Null.List) annotation).value();
		else if (annotation instanceof Past.List)
			list = ((Past.List) annotation).value();
		else if (annotation instanceof Pattern.List)
			list = ((Pattern.List) annotation).value();
		else if (annotation instanceof Size.List) list = ((Size.List) annotation).value();

		if (list != null)
			for (final Annotation anno : list)
				initializeChecks(anno, checks);
		else
		{
			LOG.warn("Ignoring unsupported JSR303 constraint annotation {1}", annotation);
			return;
		}
	}

	/**
	 * @return the applyFieldConstraintsToConstructors
	 */
	public Boolean isApplyFieldConstraintsToConstructors()
	{
		return applyFieldConstraintsToConstructors;
	}

	/**
	 * @return the applyFieldConstraintsToSetter
	 */
	public Boolean isApplyFieldConstraintsToSetter()
	{
		return applyFieldConstraintsToSetters;
	}

	/**
	 * @param applyFieldConstraintsToConstructors the applyFieldConstraintsToConstructors to set
	 */
	public void setApplyFieldConstraintsToConstructors(final Boolean applyFieldConstraintsToConstructors)
	{
		this.applyFieldConstraintsToConstructors = applyFieldConstraintsToConstructors;
	}

	/**
	 * @param applyFieldConstraintsToSetters the applyFieldConstraintsToSetter to set
	 */
	public void setApplyFieldConstraintsToSetters(final Boolean applyFieldConstraintsToSetters)
	{
		this.applyFieldConstraintsToSetters = applyFieldConstraintsToSetters;
	}
}
