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
package net.sf.oval.configuration;

import net.sf.oval.configuration.pojo.elements.ClassConfiguration;
import net.sf.oval.configuration.pojo.elements.ConstraintSetConfiguration;
import net.sf.oval.exception.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
 */
public interface Configurer
{
	/**
	 * Returns the constraint configurations for the given class. This method
	 * is invoked only once by the Validator, the very first time an object
	 * of the given class needs to be validated. The constraint configuration
	 * is then translated into an Validator internal format and cached.
	 * 
	 * @param clazz the class to get the configuration for
	 * @return The constraint configurations for the given class.
	 * @throws InvalidConfigurationException in case of illegal configuration settings
	 */
	ClassConfiguration getClassConfiguration(Class< ? > clazz) throws InvalidConfigurationException;

	/**
	 * Returns the constraint configuration for the constraint set with the 
	 * given Id.
	 * @param constraintSetId the ID of the constraint set 
	 * @return The constraint configuration for the constraint set with the given Id.
	 * @throws InvalidConfigurationException in case of illegal configuration settings
	 */
	ConstraintSetConfiguration getConstraintSetConfiguration(String constraintSetId)
			throws InvalidConfigurationException;
}
