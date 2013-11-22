/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
import java.lang.reflect.Method;
import java.util.WeakHashMap;

import net.sf.oval.internal.Log;

/**
 * Serializable Wrapper for java.lang.reflect.Method objects since they do not implement Serializable
 * 
 * @author Sebastian Thomschke
 */
public final class SerializableMethod implements Serializable
{
	private static final Log LOG = Log.getLog(SerializableMethod.class);

	private static final WeakHashMap<Method, SerializableMethod> CACHE = new WeakHashMap<Method, SerializableMethod>();

	private static final long serialVersionUID = 1L;

	public static SerializableMethod getInstance(final Method method)
	{
		/*
		 * intentionally the following code is not synchronized
		 */
		SerializableMethod sm = CACHE.get(method);
		if (sm == null)
		{
			sm = new SerializableMethod(method);
			CACHE.put(method, sm);
		}
		return sm;
	}

	private final Class< ? > declaringClass;
	private transient Method method;
	private final String name;

	private final Class< ? >[] parameterTypes;

	private SerializableMethod(final Method method)
	{
		this.method = method;
		name = method.getName();
		parameterTypes = method.getParameterTypes();
		declaringClass = method.getDeclaringClass();
	}

	/**
	 * @return the declaringClass
	 */
	public Class< ? > getDeclaringClass()
	{
		return declaringClass;
	}

	/**
	 * @return the method
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
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
			method = declaringClass.getDeclaredMethod(name, parameterTypes);
		}
		catch (final NoSuchMethodException ex)
		{
			LOG.debug("Unexpected NoSuchMethodException occured.", ex);
			throw new IOException(ex.getMessage());
		}
	}
}
