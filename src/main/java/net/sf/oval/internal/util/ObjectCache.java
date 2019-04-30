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

import java.lang.ref.SoftReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Sebastian Thomschke
 */
public abstract class ObjectCache<K, V> {
   private final ConcurrentMap<K, SoftReference<V>> map = new ConcurrentHashMap<K, SoftReference<V>>();

   public void compact() {
      for (final Map.Entry<K, SoftReference<V>> entry : map.entrySet()) {
         final SoftReference<V> ref = entry.getValue();
         if (ref.get() == null) {
            map.remove(entry.getKey());
         }
      }
   }

   public boolean contains(final K key) {
      return map.containsKey(key);
   }

   protected abstract V load(K key);

   public V get(final K key) {
      final SoftReference<V> softRef = map.get(key);
      V result = null;
      if (softRef != null) {
         final V value = softRef.get();
         if (value == null) {
            map.remove(key);
         }
         result = softRef.get();
      }
      if (result == null) {
         result = load(key);
         map.remove(key);
         map.put(key, new SoftReference<V>(result));
      }
      return result;
   }

}
