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
package net.sf.oval.checks;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.constraints.NotNegative;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class NotNegativeCheck extends AbstractAnnotationCheck<NotNegative>
{
	private static final long serialVersionUID = 1L;
	
	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		if (value instanceof Number)
		{
			if (value instanceof Float || value instanceof Double)
			{
				return ((Number) value).doubleValue() >= 0;
			}
			return ((Number) value).longValue() >= 0;
		}

		final String stringValue = value.toString();
		try
		{
			return Double.parseDouble(stringValue) >= 0;
		}
		catch (NumberFormatException e)
		{
			return false;
		}
	}
}
