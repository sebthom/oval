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

import java.io.Serializable;
import java.util.Set;

import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.exceptions.OValException;

/**
 * @author Sebastian Thomschke
 */
public class POJOConfigurer implements Configurer, Serializable
{
	private static final long serialVersionUID = 1L;
	
	protected Set<ClassConfiguration> classConfigurations;
	protected Set<ConstraintSetConfiguration> constraintSetConfigurations;

	public ClassConfiguration getClassConfiguration(final Class< ? > clazz)
	{
		if (classConfigurations != null)
		{
			for (final ClassConfiguration classConfig : classConfigurations)
			{
				if (classConfig.type == clazz) return classConfig;
			}
		}
		return null;
	}

	/**
	 * @return the classConfigurations
	 */
	public Set<ClassConfiguration> getClassConfigurations()
	{
		return classConfigurations;
	}

	public ConstraintSetConfiguration getConstraintSetConfiguration(final String constraintSetId)
			throws OValException
	{
		if (constraintSetConfigurations == null) return null;

		for (final ConstraintSetConfiguration csc : constraintSetConfigurations)
		{
			if (constraintSetId.equals(csc.id)) return csc;
		}
		return null;
	}

	/**
	 * @return the constraintSetConfigurations
	 */
	public Set<ConstraintSetConfiguration> getConstraintSetConfigurations()
	{
		return constraintSetConfigurations;
	}

	/**
	 * @param classConfigurations the classConfigurations to set
	 */
	public void setClassConfigurations(final Set<ClassConfiguration> classConfigurations)
	{
		this.classConfigurations = classConfigurations;
	}

	/**
	 * @param constraintSetConfigurations the constraintSetConfigurations to set
	 */
	public void setConstraintSetConfigurations(
			final Set<ConstraintSetConfiguration> constraintSetConfigurations)
	{
		this.constraintSetConfigurations = constraintSetConfigurations;
	}
}
