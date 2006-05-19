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

import net.sf.oval.AbstractCheck;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
public class NotEmptyCheck extends AbstractCheck<NotEmpty>
{
	public boolean isSatisfied(final Object validatedObject, final Object value)
	{
		if (value == null) return true;

		return value != null && value.toString().length() > 0;
	}
}
