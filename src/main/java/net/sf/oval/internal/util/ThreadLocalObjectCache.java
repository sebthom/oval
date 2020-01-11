/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal.util;

/**
 * @author Sebastian Thomschke
 */
public abstract class ThreadLocalObjectCache<K, V> extends ThreadLocal<ObjectCache<K, V>> {

   @Override
   public ObjectCache<K, V> initialValue() {
      return new ObjectCache<K, V>() {
         @Override
         protected V load(final K key) {
            return ThreadLocalObjectCache.this.load(key);
         }
      };
   }

   protected abstract V load(K key);

}
