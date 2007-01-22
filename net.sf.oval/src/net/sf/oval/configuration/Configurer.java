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

import net.sf.oval.configuration.elements.ClassConfiguration;
import net.sf.oval.configuration.elements.ConstraintSetConfiguration;
import net.sf.oval.exceptions.OValException;

/**
 * @author Sebastian Thomschke
 */
public interface Configurer
{
	ClassConfiguration getClassConfiguration(Class< ? > clazz) throws OValException;

	ConstraintSetConfiguration getConstraintSetConfiguration(String constraintSetId)
			throws OValException;
}
