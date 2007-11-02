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
package net.sf.oval.internal.util;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import net.sf.oval.internal.CollectionFactoryHolder;

public class IdentitySet<E> implements Set<E>, Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Map<Integer, E> map;

	/**
	 * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public IdentitySet()
	{
		map = CollectionFactoryHolder.getFactory().createMap();
	}

	/**
	 * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
	 * the given initial capacity and the default load factor (0.75).
	 */
	public IdentitySet(final int initialCapacity)
	{
		map = CollectionFactoryHolder.getFactory().createMap(initialCapacity);
	}

	public boolean add(final E o)
	{
		final int hash = System.identityHashCode(o);
		return map.put(hash, o) == null;
	}

	public boolean addAll(final Collection< ? extends E> c)
	{
		int count = 0;
		for (final E e : c)
		{
			if (add(e)) count++;
		}
		return count > 0;
	}

	public void clear()
	{
		map.clear();
	}

	public boolean contains(final Object o)
	{
		final int hash = System.identityHashCode(o);
		return map.containsKey(hash);
	}

	public boolean containsAll(final Collection< ? > c)
	{
		throw new UnsupportedOperationException();
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return map.values().iterator();
	}

	/**
	 * Reconstitute the <tt>IdentitySet</tt> instance from a stream (that is,
	 * deserialize it).
	 */
	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream ois) throws java.io.IOException,
			ClassNotFoundException
	{
		// materialize any hidden serialization magic
		ois.defaultReadObject();

		// materialize the size
		final int size = ois.readInt();

		// materialize the elements
		map = CollectionFactoryHolder.getFactory().createMap(size);
		for (int i = 0; i < size; i++)
		{
			final E o = (E) ois.readObject();
			final int hash = System.identityHashCode(o);
			map.put(hash, o);
		}
	}

	public boolean remove(final Object o)
	{
		final int hash = System.identityHashCode(o);
		return map.remove(hash) != null;
	}

	public boolean removeAll(final Collection< ? > c)
	{
		boolean modified = false;
		for (final Object e : c)
		{
			if (remove(e)) modified = true;
		}
		return modified;
	}

	public boolean retainAll(final Collection< ? > c)
	{
		throw new UnsupportedOperationException();
	}

	public int size()
	{
		return map.size();
	}

	public Object[] toArray()
	{
		return map.values().toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return map.values().toArray(a);
	}

	/**
	 * Save the state of this <tt>IdentitySet</tt> instance to a stream (that is,
	 * serialize this set).
	 */
	private void writeObject(final ObjectOutputStream oos) throws java.io.IOException
	{
		// serialize any hidden serialization magic
		oos.defaultWriteObject();

		// serialize the set's size
		oos.writeInt(map.size());

		// serialize the set's elements
		for (final E e : map.values())
		{
			oos.writeObject(e);
		}
	}
}