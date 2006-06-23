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
package net.sf.oval.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.0 $
 */
public class ThreadLocalList<T> extends ThreadLocal
{
	public Object initialValue()
	{
		return new ArrayList<T>();
	}

	@SuppressWarnings("unchecked")
	public List<T> getList()
	{
		return (List<T>) super.get();
	}
}
