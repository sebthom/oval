/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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

import static net.sf.oval.Validator.getCollectionFactory;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author Sebastian Thomschke
 */
public final class IdentitySet<E> implements Set<E>, Serializable
{
	private static final long serialVersionUID = 1L;

	private transient Map<Integer, E> map;

	/**
	 * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public IdentitySet()
	{
		map = getCollectionFactory().createMap();
	}

	/**
	 * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
	 * the given initial capacity and the default load factor (0.75).
	 */
	public IdentitySet(final int initialCapacity)
	{
		map = getCollectionFactory().createMap(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean add(final E o)
	{
		final int hash = System.identityHashCode(o);
		return map.put(hash, o) == null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(final Collection< ? extends E> c)
	{
		int count = 0;
		for (final E e : c)
			if (add(e)) count++;
		return count > 0;
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		map.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Object o)
	{
		final int hash = System.identityHashCode(o);
		return map.containsKey(hash);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsAll(final Collection< ? > c)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> iterator()
	{
		return map.values().iterator();
	}

	/**
	 * Reads the <tt>IdentitySet</tt> instance from a stream.
	 */
	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException
	{
		// materialize any hidden serialization magic
		ois.defaultReadObject();

		// materialize the size
		final int size = ois.readInt();

		// materialize the elements
		map = getCollectionFactory().createMap(size);
		for (int i = 0; i < size; i++)
		{
			final E o = (E) ois.readObject();
			final int hash = System.identityHashCode(o);
			map.put(hash, o);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(final Object o)
	{
		final int hash = System.identityHashCode(o);
		return map.remove(hash) != null;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(final Collection< ? > c)
	{
		boolean modified = false;
		for (final Object e : c)
			if (remove(e)) modified = true;
		return modified;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean retainAll(final Collection< ? > c)
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()
	{
		return map.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray()
	{
		return map.values().toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(final T[] a)
	{
		return map.values().toArray(a);
	}

	/**
	 * Writes state of this <tt>IdentitySet</tt> instance to a stream.
	 */
	private void writeObject(final ObjectOutputStream oos) throws java.io.IOException
	{
		// serialize any hidden serialization magic
		oos.defaultWriteObject();

		// serialize the set's size
		oos.writeInt(map.size());

		// serialize the set's elements
		for (final E e : map.values())
			oos.writeObject(e);
	}
}