/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
package net.sf.oval.ogn;

import net.sf.oval.exception.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
 */
public interface ObjectGraphNavigator
{
	/**
	 * Navigates through the object graph starting at <code>root</code> object.
	 * 
	 * @param root the root object to start the navigation from
	 * @param path the object navigation path relative to the root object. The path format is implementation specific. 
	 * @return the result of the navigation operation. <code>null</code> is returned if the target could not be determined, e.g. because of null values in the path.
	 * @throws InvalidConfigurationException if the given path is invalid, e.g. because of non-existing fields/properties named in the path.  
	 */
	ObjectGraphNavigationResult navigateTo(final Object root, final String path) throws InvalidConfigurationException;
}
