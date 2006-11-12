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
package net.sf.oval.constraints;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertValidCheck extends AbstractAnnotationCheck<AssertValid>
{
	private static final long serialVersionUID = 1L;
	
	/**
	 *  This method is not used.
	 *  The validation of this special constraint is directly performed by the Validator class
	 */
	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		// ignored
		return true;
	}
}
