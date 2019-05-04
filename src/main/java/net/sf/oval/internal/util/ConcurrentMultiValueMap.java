/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public class ConcurrentMultiValueMap<K, V> {

   private static final Set<?> EMPTY_SET = Collections.unmodifiableSet(new HashSet<Object>());

   public static <K, V> ConcurrentMultiValueMap<K, V> create() {
      return new ConcurrentMultiValueMap<K, V>();
   }

   private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
   private final Map<K, Set<V>> map = Validator.getCollectionFactory().createMap();

   public boolean add(final K key, final V value) {
      rwLock.writeLock().lock();
      try {
         Set<V> set = map.get(key);
         if (set == null) {
            set = new LinkedHashSet<V>();
            map.put(key, set);
         }
         return set.add(value);
      } finally {
         rwLock.writeLock().unlock();
      }
   }

   public void addAllTo(final K key, final Collection<V> coll) {
      rwLock.readLock().lock();
      try {
         final Set<V> set = map.get(key);
         if (set == null)
            return;
         coll.addAll(set);
      } finally {
         rwLock.readLock().unlock();
      }
   }

   public boolean containsKey(final K key) {
      rwLock.readLock().lock();
      try {
         return map.containsKey(key);
      } finally {
         rwLock.readLock().unlock();
      }
   }

   public boolean containsValue(final K key, final V value) {
      rwLock.readLock().lock();
      try {
         final Set<V> set = map.get(key);
         if (set == null)
            return false;
         return set.contains(value);
      } finally {
         rwLock.readLock().unlock();
      }
   }

   @SuppressWarnings("unchecked")
   public Iterable<V> get(final K key) {
      rwLock.readLock().lock();
      try {
         final Set<V> set = map.get(key);
         if (set == null)
            return (Set<V>) EMPTY_SET;
         return new LinkedHashSet<V>(set);
      } finally {
         rwLock.readLock().unlock();
      }
   }

   public boolean remove(final K key, final V value) {
      rwLock.writeLock().lock();
      try {
         final Set<V> set = map.get(key);
         if (set == null)
            return false;
         final boolean removed = set.remove(value);
         if (set.isEmpty()) {
            map.remove(key);
         }
         return removed;
      } finally {
         rwLock.writeLock().unlock();
      }
   }
}
