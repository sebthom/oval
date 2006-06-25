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
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
public class InstanceOfCheck extends AbstractCheck<InstanceOf>
{
	private Class clazz;

	public boolean isSatisfied(Object validatedObject, Object value, final OValContext context)
	{
		if (value == null) return true;

		return clazz.isInstance(value);
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{clazz.getName()};
	}

}
