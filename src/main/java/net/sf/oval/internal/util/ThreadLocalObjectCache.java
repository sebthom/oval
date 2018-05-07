/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
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
public final class ThreadLocalObjectCache<K, V> extends ThreadLocal<ObjectCache<K, V>> {
    private final int maxElementsToKeep;

    public ThreadLocalObjectCache() {
        this.maxElementsToKeep = -1;
    }

    public ThreadLocalObjectCache(final int maxElementsToKeep) {
        this.maxElementsToKeep = maxElementsToKeep;
    }

    @Override
    public ObjectCache<K, V> initialValue() {
        return new ObjectCache<K, V>(maxElementsToKeep);
    }
}
