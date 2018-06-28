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

import static net.sf.oval.Validator.*;

import java.util.Map;

/**
 * @author Sebastian Thomschke
 */
public final class ThreadLocalMap<K, V> extends ThreadLocal<Map<K, V>> {

   @Override
   public Map<K, V> initialValue() {
      return getCollectionFactory().createMap();
   }
}
