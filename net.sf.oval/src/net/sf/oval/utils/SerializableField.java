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
package net.sf.oval.utils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.WeakHashMap;

import net.sf.oval.exceptions.NestableIOException;

/**
 * Serializable Wrapper for java.lang.reflect.Field objects since they do not implement Serializable
 * 
 * @author Sebastian Thomschke
 */
public class SerializableField implements Serializable
{
	private static final WeakHashMap<Field, SerializableField> CACHE = new WeakHashMap<Field, SerializableField>();

	private static final long serialVersionUID = 1L;

	public static SerializableField getInstance(final Field field)
	{
		/*
		 * intentionally the following code is not synchronized
		 */
		SerializableField sm = CACHE.get(field);
		if (sm == null)
		{
			sm = new SerializableField(field);
			CACHE.put(field, sm);
		}
		return sm;
	}

	private final Class< ? > declaringClass;
	private transient Field field;
	private final String name;

	protected SerializableField(final Field field)
	{
		this.field = field;
		this.name = field.getName();
		this.declaringClass = field.getDeclaringClass();
	}

	/**
	 * @return the declaringClass
	 */
	public Class< ? > getDeclaringClass()
	{
		return declaringClass;
	}

	/**
	 * @return the field
	 */
	public Field getField()
	{
		return field;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	private void readObject(final java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{
		in.defaultReadObject();
		try
		{
			field = declaringClass.getField(name);
		}
		catch (SecurityException e)
		{
			throw new NestableIOException(e);
		}
		catch (NoSuchFieldException e)
		{
			throw new NestableIOException(e);
		}
	}
}
