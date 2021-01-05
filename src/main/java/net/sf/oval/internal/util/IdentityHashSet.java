/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * @author Sebastian Thomschke
 */
public final class IdentityHashSet<E> implements Set<E>, Serializable {
   private static final long serialVersionUID = 1L;

   private final IdentityHashMap<E, Boolean> map;
   private transient Set<E> entries;

   /**
    * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
    * default initial capacity (16).
    */
   public IdentityHashSet() {
      map = new IdentityHashMap<>(16);
      entries = map.keySet();
   }

   /**
    * Constructs a new, empty <tt>IdentitySet</tt>; the backing <tt>Map</tt> instance has
    * the given initial capacity.
    */
   public IdentityHashSet(final int initialCapacity) {
      map = new IdentityHashMap<>(initialCapacity);
      entries = map.keySet();
   }

   @Override
   public boolean add(final E o) {
      return map.put(o, Boolean.TRUE) == null;
   }

   @Override
   public boolean addAll(final Collection<? extends E> c) {
      boolean modified = false;
      for (final E e : c)
         if (add(e)) {
            modified = true;
         }
      return modified;
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public boolean contains(final Object o) {
      return map.containsKey(o);
   }

   @Override
   public boolean containsAll(final Collection<?> c) {
      return entries.containsAll(c);
   }

   @Override
   public boolean equals(final Object o) {
      return o == this || entries.equals(o);
   }

   @Override
   public void forEach(final Consumer<? super E> action) {
      entries.forEach(action);
   }

   @Override
   public int hashCode() {
      return entries.hashCode();
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Iterator<E> iterator() {
      return entries.iterator();
   }

   @Override
   public Stream<E> parallelStream() {
      return entries.parallelStream();
   }

   private void readObject(final java.io.ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      entries = map.keySet();
   }

   @Override
   public boolean remove(final Object o) {
      return map.remove(o) != null;
   }

   @Override
   public boolean removeAll(final Collection<?> c) {
      return entries.removeAll(c);
   }

   @Override
   public boolean removeIf(final Predicate<? super E> filter) {
      return entries.removeIf(filter);
   }

   @Override
   public boolean retainAll(final Collection<?> c) {
      return entries.retainAll(c);
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Spliterator<E> spliterator() {
      return entries.spliterator();
   }

   @Override
   public Stream<E> stream() {
      return entries.stream();
   }

   @Override
   public Object[] toArray() {
      return entries.toArray();
   }

   @Override
   public <T> T[] toArray(final T[] a) {
      return map.keySet().toArray(a);
   }

   @Override
   public String toString() {
      return entries.toString();
   }
}
