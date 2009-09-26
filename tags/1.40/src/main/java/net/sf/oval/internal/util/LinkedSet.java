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

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

/**
 *  @author Sebastian Thomschke
 */
public class LinkedSet<E> implements Cloneable, Set<E>, List<E>
{
	private final List<E> list;

	/**
	 * Constructs a new, empty collection with an initial capacity (16).
	 */
	public LinkedSet()
	{
		list = getCollectionFactory().createList(16);
	}

	/**
	 * Constructs a new, empty collection with the given initial capacity.
	 *
	 * @param initialCapacity the initial capacity
	 */
	public LinkedSet(final int initialCapacity)
	{
		list = getCollectionFactory().createList(initialCapacity);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean add(final E e)
	{
		return list.add(e);
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(final int index, final E e)
	{
		if (!list.contains(e))
		{
			list.add(index, e);
		}
	}

	/**
	 * {@inheritDoc}
	 */
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

	/**
	 * {@inheritDoc}
	 */
	public boolean addAll(final int index, final Collection< ? extends E> c)
	{
		final List<E> tmp = getCollectionFactory().createList(c.size());
		for (final E o : c)
		{
			if (!list.contains(o))
			{
				tmp.add(o);
			}
		}
		return list.addAll(index, tmp);
	}

	/**
	 * {@inheritDoc}
	 */
	public void clear()
	{
		list.clear();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object clone() throws CloneNotSupportedException
	{
		@SuppressWarnings("unchecked")
		final LinkedSet<E> os = (LinkedSet<E>) super.clone();
		os.list.addAll(list);
		return os;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean contains(final Object o)
	{
		return list.contains(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean containsAll(final Collection< ? > c)
	{
		return list.containsAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		@SuppressWarnings("unchecked")
		final LinkedSet other = (LinkedSet) obj;
		if (list == null)
		{
			if (other.list != null) return false;
		}
		else if (!list.equals(other.list)) return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public E get(final int index)
	{
		return list.get(index);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + (list == null ? 0 : list.hashCode());
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	public int indexOf(final Object o)
	{
		return list.indexOf(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEmpty()
	{
		return list.isEmpty();
	}

	/**
	 * {@inheritDoc}
	 */
	public Iterator<E> iterator()
	{
		return list.iterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public int lastIndexOf(final Object o)
	{
		return list.lastIndexOf(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public ListIterator<E> listIterator()
	{
		return list.listIterator();
	}

	/**
	 * {@inheritDoc}
	 */
	public ListIterator<E> listIterator(final int index)
	{
		return list.listIterator(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public E remove(final int index)
	{
		return list.remove(index);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean remove(final Object o)
	{
		return list.remove(o);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean removeAll(final Collection< ? > c)
	{
		return list.removeAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean retainAll(final Collection< ? > c)
	{
		return list.retainAll(c);
	}

	/**
	 * {@inheritDoc}
	 */
	public E set(final int index, final E element)
	{
		final int elementIndex = list.indexOf(element);

		if (elementIndex == index) return element;

		if (elementIndex == -1) return list.set(index, element);
		if (elementIndex > index)
		{
			list.remove(element);
			return list.set(index, element);
		}

		// if (elementIndex < index)
		list.remove(element);
		return list.set(index - 1, element);
	}

	/**
	 * {@inheritDoc}
	 */
	public int size()
	{
		return list.size();
	}

	/**
	 * {@inheritDoc}
	 */
	public List<E> subList(final int fromIndex, final int toIndex)
	{
		return list.subList(fromIndex, toIndex);
	}

	/**
	 * {@inheritDoc}
	 */
	public Object[] toArray()
	{
		return list.toArray();
	}

	/**
	 * {@inheritDoc}
	 */
	public <T> T[] toArray(final T[] a)
	{
		return list.toArray(a);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString()
	{
		return list.toString();
	}
}