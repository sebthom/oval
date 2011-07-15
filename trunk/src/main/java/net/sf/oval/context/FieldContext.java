/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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

import net.sf.oval.internal.util.ReflectionUtils;
import net.sf.oval.internal.util.SerializableField;

/**
 * @author Sebastian Thomschke
 */
public class FieldContext extends OValContext
{
	private static final long serialVersionUID = 1L;

	private final SerializableField field;

	/**
	 * @param declaringClass
	 * @param fieldName
	 */
	public FieldContext(final Class< ? > declaringClass, final String fieldName)
	{
		final Field field = ReflectionUtils.getField(declaringClass, fieldName);
		this.field = SerializableField.getInstance(field);
		compileTimeType = field.getType();
	}

	/**
	 * @param field
	 */
	public FieldContext(final Field field)
	{
		this.field = SerializableField.getInstance(field);
		compileTimeType = field.getType();
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
