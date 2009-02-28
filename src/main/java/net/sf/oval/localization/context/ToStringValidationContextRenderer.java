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

import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class ToStringValidationContextRenderer implements OValContextRenderer
{
	public static final ToStringValidationContextRenderer INSTANCE = new ToStringValidationContextRenderer();

	/**
	 * {@inheritDoc}
	 */
	public String render(final OValContext ovalContext)
	{
		return ovalContext.toString();
	}
}
