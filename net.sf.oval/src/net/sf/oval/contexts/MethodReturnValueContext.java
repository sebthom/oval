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
package net.sf.oval.contexts;

import java.lang.reflect.Method;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public class MethodReturnValueContext extends OValContext
{
	private final Method getter;

	public MethodReturnValueContext(final Method getter)
	{
		this.getter = getter;
	}

	/**
	 * @return Returns the getter.
	 */
	public Method getGetter()
	{
		return getter;
	}

	public String toString()
	{
		return getter.getDeclaringClass().getName() + "." + getter.getName() + "()";
	}

}
