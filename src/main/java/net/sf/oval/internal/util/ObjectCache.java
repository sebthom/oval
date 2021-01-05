/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * @author Sebastian Thomschke
 */
public final class ObjectCache<K, V> {

   private static final class SoftRef<K, V> extends SoftReference<V> {
      final K key;

      SoftRef(final K key, final V value, final ReferenceQueue<? super V> q) {
         super(value, q);
         this.key = key;
      }
   }

   private final ConcurrentMap<K, SoftRef<K, V>> map = new ConcurrentHashMap<>();
   private final ReferenceQueue<V> garbageCollectedValues = new ReferenceQueue<>();

   private final Function<K, V> loader;

   public ObjectCache(final Function<K, V> loader) {
      Assert.argumentNotNull("loader", loader);
      this.loader = loader;
   }

   @SuppressWarnings("unchecked")
   private void compact() {
      for (Reference<?> ref; (ref = garbageCollectedValues.poll()) != null;) { // CHECKSTYLE:IGNORE .*
         map.remove(((SoftRef<K, V>) ref).key, ref);
      }
   }

   public boolean contains(final K key) {
      return map.containsKey(key);
   }

   public V get(final K key) {
      compact();

      final SoftRef<K, V> softRef = map.get(key);
      V result = null;
      if (softRef != null) {
         final V value = softRef.get();
         if (value == null) {
            map.remove(key);
         }
         result = softRef.get();
      }
      if (result == null) {
         result = loader.apply(key);
         map.remove(key);
         map.put(key, new SoftRef<>(key, result, garbageCollectedValues));
      }
      return result;
   }
}
