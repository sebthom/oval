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
package net.sf.oval.configuration;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import net.sf.oval.Check;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.FieldConfiguration;
import net.sf.oval.constraints.LengthCheck;
import net.sf.oval.constraints.NotNullCheck;
import net.sf.oval.exceptions.OValException;

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
	private Boolean applyFieldConstraintsToSetter;

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
			final List<Check> checks = CollectionFactory.INSTANCE.createList(4);

			// loop over all annotations of the current field
			for (final Annotation annotation : field.getAnnotations())
			{
				if (annotation instanceof Basic)
				{
					initializeChecks((Basic) annotation, checks);
				}
				else if (annotation instanceof Column)
				{
					initializeChecks((Column) annotation, checks);
				}
				else if (annotation instanceof OneToOne)
				{
					initializeChecks((OneToOne) annotation, checks);
				}
				else if (annotation instanceof ManyToOne)
				{
					initializeChecks((ManyToOne) annotation, checks);
				}
			}
			if (checks.size() > 0)
			{
				if (config.fieldConfigurations == null)
					config.fieldConfigurations = CollectionFactory.INSTANCE.createSet(8);

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

	private void initializeChecks(final Basic annotation, final Collection<Check> checks)
	{
		if (!annotation.optional())
		{
			checks.add(new NotNullCheck());
		}
	}

	private void initializeChecks(final Column annotation, final Collection<Check> checks)
	{
		if (!annotation.nullable())
		{
			checks.add(new NotNullCheck());
		}
		final LengthCheck lengthCheck = new LengthCheck();
		lengthCheck.setMax(annotation.length());
		checks.add(lengthCheck);
	}

	private void initializeChecks(final ManyToOne annotation, final Collection<Check> checks)
	{
		if (!annotation.optional())
		{
			checks.add(new NotNullCheck());
		}
	}

	private void initializeChecks(final OneToOne annotation, final Collection<Check> checks)
	{
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
