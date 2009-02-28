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
package net.sf.oval.internal.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.WeakHashMap;

import net.sf.oval.internal.Log;

/**
 * Serializable Wrapper for java.lang.reflect.Constructor objects since they do not implement Serializable
 * 
 * @author Sebastian Thomschke
 */
public class SerializableConstructor implements Serializable
{
	private static final Log LOG = Log.getLog(SerializableConstructor.class);

	private static final WeakHashMap<Constructor< ? >, SerializableConstructor> CACHE = //
	new WeakHashMap<Constructor< ? >, SerializableConstructor>();

	private static final long serialVersionUID = 1L;

	public static SerializableConstructor getInstance(final Constructor< ? > constructor)
	{
		/*
		 * intentionally the following code is not synchronized
		 */
		SerializableConstructor sm = CACHE.get(constructor);
		if (sm == null)
		{
			sm = new SerializableConstructor(constructor);
			CACHE.put(constructor, sm);
		}
		return sm;
	}

	private transient Constructor< ? > constructor;

	private final Class< ? > declaringClass;

	private final Class< ? >[] parameterTypes;

	protected SerializableConstructor(final Constructor< ? > constructor)
	{
		this.constructor = constructor;
		parameterTypes = constructor.getParameterTypes();
		declaringClass = constructor.getDeclaringClass();
	}

	/**
	 * @return the constructor
	 */
	public Constructor< ? > getConstructor()
	{
		return constructor;
	}

	/**
	 * @return the declaringClass
	 */
	public Class< ? > getDeclaringClass()
	{
		return declaringClass;
	}

	/**
	 * @return the parameterTypes
	 */
	public Class< ? >[] getParameterTypes()
	{
		return parameterTypes;
	}

	private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		in.defaultReadObject();
		try
		{
			constructor = declaringClass.getDeclaredConstructor(parameterTypes);
		}
		catch (final NoSuchMethodException ex)
		{
			LOG.debug("Unexpected NoSuchMethodException occured", ex);
			throw new IOException(ex.getMessage());
		}
	}
}
