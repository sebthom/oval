/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
public interface OValContextRenderer
{
	String render(OValContext context);
}
