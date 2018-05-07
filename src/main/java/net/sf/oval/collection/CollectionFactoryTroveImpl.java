/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.collection;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

/**
 * @author Sebastian Thomschke
 */
public class CollectionFactoryTroveImpl implements CollectionFactory {

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
        return new THashMap<KeyType, ValueType>();
    }

    @Override
    public <KeyType, ValueType> Map<KeyType, ValueType> createMap(final int initialCapacity) {
        return new THashMap<KeyType, ValueType>(initialCapacity);
    }

    @Override
    public <ValueType> Set<ValueType> createSet() {
        return new THashSet<ValueType>();
    }

    @Override
    public <ValueType> Set<ValueType> createSet(final int initialCapacity) {
        return new THashSet<ValueType>(initialCapacity);
    }
}
