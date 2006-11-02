package net.sf.oval.utils;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.WeakHashMap;

/**
 * Serializable Wrapper for java.lang.reflect.Constructor objects since they do not implement Serializable
 * 
 * @author Sebastian Thomschke
 */
public class SerializableConstructor implements Serializable
{
	private static final WeakHashMap<Constructor< ? >, SerializableConstructor> CACHE = new WeakHashMap<Constructor< ? >, SerializableConstructor>();

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
		this.parameterTypes = constructor.getParameterTypes();
		this.declaringClass = constructor.getDeclaringClass();
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

	private void readObject(final java.io.ObjectInputStream in) throws IOException,
			ClassNotFoundException
	{
		in.defaultReadObject();
		try
		{
			constructor = declaringClass.getConstructor(parameterTypes);
		}
		catch (SecurityException e)
		{
			throw new IOException(e);
		}
		catch (NoSuchMethodException e)
		{
			throw new IOException(e);
		}
	}
}
