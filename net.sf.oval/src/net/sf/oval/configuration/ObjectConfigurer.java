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

import java.util.Set;

import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.configuration.elements.OValConfiguration;

/**
 * @author Sebastian Thomschke
 */
public class ObjectConfigurer implements Configurer
{
	protected OValConfiguration oValConfiguration;

	public ClassConfiguration getClassConfiguration(final Class< ? > clazz)
	{
		if (oValConfiguration.classesConfig != null)
		{
			for (ClassConfiguration classConfig : oValConfiguration.classesConfig)
			{
				if (classConfig.type == clazz) return classConfig;
			}
		}
		return null;
	}

	public Set<ConstraintSetConfiguration> getConstraintSetConfigurations()
	{
		return oValConfiguration.constraintSetsConfig;
	}

	/**
	 * @return the oValConfiguration
	 */
	public OValConfiguration getOValConfiguration()
	{
		return oValConfiguration;
	}

	/**
	 * @param valConfiguration the oValConfiguration to set
	 */
	public void setOValConfiguration(final OValConfiguration valConfiguration)
	{
		oValConfiguration = valConfiguration;
	}
}
