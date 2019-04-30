/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.collection;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author Sebastian Thomschke
 */
public class CollectionFactoryJDKImpl implements CollectionFactory {

   @Override
   public <KeyType, ValueType> ConcurrentMap<KeyType, ValueType> createConcurrentMap() {
      return new ConcurrentHashMap<KeyType, ValueType>();
   }

   @Override
   public <KeyType, ValueType> ConcurrentMap<KeyType, ValueType> createConcurrentMap(final int initialCapacity) {
      return new ConcurrentHashMap<KeyType, ValueType>(initialCapacity);
   }

   @Override
   public <ValueType> List<ValueType> createList() {
      return new ArrayList<ValueType>();
   }

   @Override
   public <ValueType> List<ValueType> createList(final int initialCapacity) {
      return new ArrayList<ValueType>(initialCapacity);
   }

   @Override
   public <KeyType, ValueType> Map<KeyType, ValueType> createMap() {
      return new LinkedHashMap<KeyType, ValueType>();
   }

   @Override
   public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity) {
      return new LinkedHashMap<KeyType, ValueType>(initialCapacity);
   }

   @Override
   public <ValueType> Set<ValueType> createSet() {
      return new LinkedHashSet<ValueType>();
   }

   @Override
   public <ValueType> Set<ValueType> createSet(final int initialCapacity) {
      return new LinkedHashSet<ValueType>(initialCapacity);
   }
}
