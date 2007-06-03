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
package net.sf.oval.constraint;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertTrueCheck extends AbstractAnnotationCheck<AssertTrue>
{
	private static final long serialVersionUID = 1L;
	
	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator)
	{
		if (value == null) return true;

		if (value instanceof Boolean)
		{
			return ((Boolean) value).booleanValue();
		}
		return false;
	}
}