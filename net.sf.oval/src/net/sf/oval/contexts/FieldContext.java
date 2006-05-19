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

import java.lang.reflect.Field;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.2 $
 */
public class FieldContext extends OValContext
{
	private static final long serialVersionUID = 4955115715680346101L;

	private final Field field;

	/**
	 * @param field
	 */
	public FieldContext(final Field field)
	{
		this.field = field;
	}

	/**
	 * @return Returns the field.
	 */
	public Field getField()
	{
		return field;
	}

	public String toString()
	{
		return field.getDeclaringClass().getName() + "." + field.getName();
	}
}
