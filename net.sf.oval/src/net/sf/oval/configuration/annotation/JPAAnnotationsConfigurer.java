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
package net.sf.oval.configuration.annotation;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Version;

import net.sf.oval.Check;
import net.sf.oval.collection.CollectionFactoryHolder;
import net.sf.oval.configuration.Configurer;
import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.pojo.elements.FieldConfiguration;
import net.sf.oval.constraint.LengthCheck;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.exception.OValException;

/**
 * Constraints configurer that interprets certain EJB3 JPA annotations:
 * <ul>
 * <li>javax.persistence.Basic(optional=false)     => net.sf.oval.constraints.NotNullCheck
 * <li>javax.persistence.OneToOne(optional=false)  => net.sf.oval.constraints.NotNullCheck
 * <li>javax.persistence.ManyToOne(optional=false) => net.sf.oval.constraints.NotNullCheck
 * <li>javax.persistence.Column(nullable=false)    => net.sf.oval.constraints.NotNullCheck
 * <li>javax.persistence.Column(length=5)          => net.sf.oval.constraints.LengthCheck
 * </ul>
 * @author Sebastian Thomschke
 */
public class JPAAnnotationsConfigurer implements Configurer
{
	protected Boolean applyFieldConstraintsToSetter;

	public ClassConfiguration getClassConfiguration(final Class< ? > clazz) throws OValException
	{
		final ClassConfiguration config = new ClassConfiguration();
		config.type = clazz;
		config.applyFieldConstraintsToSetter = applyFieldConstraintsToSetter;

		/*
		 * determine field checks
		 */
		for (final Field field : config.type.getDeclaredFields())
		{
			final List<Check> checks = CollectionFactoryHolder.getFactory().createList(4);

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
			{
				if (annotation instanceof Basic)
				{
					initializeChecks((Basic) annotation, checks, field);
				}
				else if (annotation instanceof Column)
				{
					initializeChecks((Column) annotation, checks, field);
				}
				else if (annotation instanceof OneToOne)
				{
					initializeChecks((OneToOne) annotation, checks, field);
				}
				else if (annotation instanceof ManyToOne)
				{
					initializeChecks((ManyToOne) annotation, checks, field);
				}
			}
			if (checks.size() > 0)
			{
				if (config.fieldConfigurations == null)
					config.fieldConfigurations = CollectionFactoryHolder.getFactory().createSet(8);

				final FieldConfiguration fc = new FieldConfiguration();
				fc.name = field.getName();
				fc.checks = checks;
				config.fieldConfigurations.add(fc);
			}
		}
		return config;
	}

	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
			throws OValException
	{
		return null;
	}

	protected void initializeChecks(final Basic annotation, final Collection<Check> checks,
			final Field field)
	{
		assert annotation != null;
		assert checks != null;

		if (!annotation.optional())
		{
			checks.add(new NotNullCheck());
		}
	}

	protected void initializeChecks(final Column annotation, final Collection<Check> checks,
			final Field field)
	{
		assert annotation != null;
		assert checks != null;

		/* If the value is generated (annotated with @GeneratedValue) it is allowed to be null 
		 * before the entity has been persisted, same is true in case of optimistic locking
		 * when a field is annotated with @Version.
		 * Therefore and because of the fact that there is no generic way to determine if an entity 
		 * has been persisted already, a not-null check will not be performed for such fields. 
		 */
		if (!annotation.nullable() && !field.isAnnotationPresent(GeneratedValue.class)
				&& !field.isAnnotationPresent(Version.class))
		{
			checks.add(new NotNullCheck());
		}

		final LengthCheck lengthCheck = new LengthCheck();
		lengthCheck.setMax(annotation.length());
		checks.add(lengthCheck);
	}

	protected void initializeChecks(final ManyToOne annotation, final Collection<Check> checks,
			final Field field)
	{
		assert annotation != null;
		assert checks != null;

		if (!annotation.optional())
		{
			checks.add(new NotNullCheck());
		}
	}

	protected void initializeChecks(final OneToOne annotation, final Collection<Check> checks,
			final Field field)
	{
		assert annotation != null;
		assert checks != null;

		if (!annotation.optional())
		{
			checks.add(new NotNullCheck());
		}
	}

	/**
	 * @return the applyFieldConstraintsToSetter
	 */
	public Boolean isApplyFieldConstraintsToSetter()
	{
		return applyFieldConstraintsToSetter;
	}

	/**
	 * @param applyFieldConstraintsToSetter the applyFieldConstraintsToSetter to set
	 */
	public void setApplyFieldConstraintsToSetter(final Boolean applyFieldConstraintsToSetter)
	{
		this.applyFieldConstraintsToSetter = applyFieldConstraintsToSetter;
	}

}
