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
package net.sf.oval.context;

import java.lang.reflect.Field;

import net.sf.oval.internal.util.SerializableField;

/**
 * @author Sebastian Thomschke
 */
public class FieldContext extends OValContext
{
	private static final long serialVersionUID = 1L;

	private final SerializableField field;

	/**
	 * @param field
	 */
	public FieldContext(final Field field)
	{
		this.field = SerializableField.getInstance(field);
		this.compileTimeType = field.getType();
	}

	/**
	 * @return Returns the field.
	 */
	public Field getField()
	{
		return field.getField();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return field.getDeclaringClass().getName() + "." + field.getName();
	}
}
