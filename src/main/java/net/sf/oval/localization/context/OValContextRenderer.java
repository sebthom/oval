/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.localization.context;

import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;

/**
 * Implementations of this interface are able to transform a validation context into a
 * human readable (and if required localized) form.
 * 
 * The implementation to be used can be set on the Validator class using the static
 * setContextRenderer(...) method.
 * 
 * @author Sebastian Thomschke
 * @see Validator
 */
public interface OValContextRenderer {
   String render(OValContext context);
}
