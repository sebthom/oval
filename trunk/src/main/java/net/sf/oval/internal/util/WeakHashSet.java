/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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

import static java.lang.Boolean.TRUE;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * @author Sebastian Thomschke
 */
public class WeakHashSet<E> implements Set<E>, Serializable
{
	private static final long serialVersionUID = 1L;

	private transient WeakHashMap<E, Object> map;

	/**
	 * Constructs a new, empty <tt>WeakHashSet</tt>; the backing <tt>WeakHashMap</tt> instance has
	 * default initial capacity (16) and load factor (0.75).
	 */
	public WeakHashSet()
	{
		map = new WeakHashMap<E, Object>();
	}

	/**
	 * Constructs a new, empty <tt>WeakHashSet</tt>; the backing <tt>WeakHashMap</tt> instance has
	 * the given initial capacity and the default load factor (0.75).
	 */
	public WeakHashSet(final int initialCapacity)
	{
		map = new WeakHashMap<E, Object>(initialCapacity);
	}

	public boolean add(final E o)
	{
		return map.put(o, TRUE) == null;
	}

	public boolean addAll(final Collection< ? extends E> c)
	{
		int count = 0;
		for (final E e : c)
		{
			if (add(e))
			{
				count++;
			}
		}
		return count > 0;
	}

	public void clear()
	{
		map.clear();
	}

	public boolean contains(final Object o)
	{
		return map.containsKey(o);
	}

	public boolean containsAll(final Collection< ? > c)
	{
		return map.keySet().containsAll(c);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(final Object o)
	{
		if (o == this) return true;

		if (!(o instanceof Set)) return false;

		if (((Set) o).size() != size()) return false;

		return containsAll((Set) o);
	}

	@Override
	public int hashCode()
	{
		int hash = 0;
		for (final E e : map.keySet())
		{
			if (e != null)
			{
				hash += e.hashCode();
			}
		}
		return hash;
	}

	public boolean isEmpty()
	{
		return map.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return map.keySet().iterator();
	}

	/**
	 * Reads the <tt>WeakHashSet</tt> instance from a stream.
	 */
	@SuppressWarnings("unchecked")
	private void readObject(final ObjectInputStream ois) throws java.io.IOException, ClassNotFoundException
	{
		// materialize any hidden serialization magic
		ois.defaultReadObject();

		// materialize the size
		final int size = ois.readInt();

		// materialize the elements
		map = new WeakHashMap<E, Object>(size);
		for (int i = 0; i < size; i++)
		{
			map.put((E) ois.readObject(), TRUE);
		}
	}

	public boolean remove(final Object o)
	{
		return map.remove(o) == TRUE;
	}

	public boolean removeAll(final Collection< ? > c)
	{
		return map.keySet().removeAll(c);
	}

	public boolean retainAll(final Collection< ? > c)
	{
		return map.keySet().retainAll(c);
	}

	public int size()
	{
		return map.size();
	}

	public Object[] toArray()
	{
		return map.keySet().toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return map.keySet().toArray(a);
	}

	/**
	 * Writes the state of this <tt>WeakHashSet</tt> instance to a stream.
	 */
	private void writeObject(final ObjectOutputStream oos) throws java.io.IOException
	{
		// serialize any hidden serialization magic
		oos.defaultWriteObject();

		// serialize the set's size
		oos.writeInt(map.size());

		// serialize the set's elements
		for (final E e : map.keySet())
		{
			oos.writeObject(e);
		}
	}
}
