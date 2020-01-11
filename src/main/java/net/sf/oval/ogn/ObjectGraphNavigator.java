/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.ogn;

import net.sf.oval.exception.InvalidConfigurationException;

/**
 * @author Sebastian Thomschke
 */
public interface ObjectGraphNavigator {
   /**
    * Navigates through the object graph starting at <code>root</code> object.
    *
    * @param root the root object to start the navigation from
    * @param path the object navigation path relative to the root object. The path format is implementation specific.
    * @return the result of the navigation operation. <code>null</code> is returned if the target could not be determined, e.g. because of null values in the
    *         path.
    * @throws InvalidConfigurationException if the given path is invalid, e.g. because of non-existing fields/properties named in the path.
    */
   ObjectGraphNavigationResult navigateTo(Object root, String path) throws InvalidConfigurationException;
}
