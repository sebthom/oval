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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.sf.oval.collection.CollectionFactoryHolder;

/**
 *  @author Sebastian Thomschke
 */
public class ListOrderedSet<E> implements Cloneable, Set<E>, List<E>
{
	private final List<E> list;

	/**
	 * Constructs a new, empty collection with an initial capacity (16).
	 */
	public ListOrderedSet()
	{
		list = CollectionFactoryHolder.getFactory().createList(16);
	}

	/**
	 * Constructs a new, empty collection with the given initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 */
	public ListOrderedSet(final int initialCapacity)
	{
		list = CollectionFactoryHolder.getFactory().createList(initialCapacity);
	}

	public boolean add(final E e)
	{
		return list.add(e);
	}

	public void add(final int index, final E e)
	{
		if (!list.contains(e)) list.add(index, e);
	}

	public boolean addAll(final Collection< ? extends E> c)
	{
		boolean itemAdded = false;
		for (final E o : c)
		{
			if (!list.contains(o))
			{
				add(o);
				itemAdded = true;
			}
		}
		return itemAdded;
	}

	public boolean addAll(final int index, final Collection< ? extends E> c)
	{
		final List<E> tmp = CollectionFactoryHolder.getFactory().createList(c.size());
		for (final E o : c)
		{
			if (!list.contains(o))
			{
				tmp.add(o);
			}
		}
		return list.addAll(index, tmp);
	}

	public void clear()
	{
		list.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		final ListOrderedSet<E> os = (ListOrderedSet<E>) super.clone();
		os.list.addAll(list);
		return os;
	}

	public boolean contains(final Object o)
	{
		return list.contains(o);
	}

	public boolean containsAll(final Collection< ? > c)
	{
		return list.containsAll(c);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		final ListOrderedSet other = (ListOrderedSet) obj;
		if (list == null)
		{
			if (other.list != null) return false;
		}
		else if (!list.equals(other.list)) return false;
		return true;
	}

	public E get(final int index)
	{
		return list.get(index);
	}

	@Override
	public int hashCode()
	{
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((list == null) ? 0 : list.hashCode());
		return result;
	}

	public int indexOf(final Object o)
	{
		return list.indexOf(o);
	}

	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	public Iterator<E> iterator()
	{
		return list.iterator();
	}

	public int lastIndexOf(final Object o)
	{
		return list.lastIndexOf(o);
	}

	public ListIterator<E> listIterator()
	{
		return list.listIterator();
	}

	public ListIterator<E> listIterator(final int index)
	{
		return list.listIterator(index);
	}

	public E remove(final int index)
	{
		return list.remove(index);
	}

	public boolean remove(final Object o)
	{
		return list.remove(o);
	}

	public boolean removeAll(final Collection< ? > c)
	{
		return list.removeAll(c);
	}

	public boolean retainAll(final Collection< ? > c)
	{
		return list.retainAll(c);
	}

	public E set(final int index, final E element)
	{
		final int elementIndex = list.indexOf(element);

		if (elementIndex == index) return element;

		if (elementIndex == -1)
		{
			return list.set(index, element);
		}
		if (elementIndex > index)
		{
			list.remove(element);
			return list.set(index, element);
		}

		// if (elementIndex < index)
		list.remove(element);
		return list.set(index - 1, element);
	}

	public int size()
	{
		return list.size();
	}

	public List<E> subList(final int fromIndex, final int toIndex)
	{
		return list.subList(fromIndex, toIndex);
	}

	public Object[] toArray()
	{
		return list.toArray();
	}

	public <T> T[] toArray(final T[] a)
	{
		return list.toArray(a);
	}

	@Override
	public String toString()
	{
		return list.toString();
	}
}