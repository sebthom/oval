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

import java.util.List;

import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public final class CollectionUtils {

   public static <T> List<T> clone(final List<T> list) {
      final List<T> clone = Validator.getCollectionFactory().createList(list.size());
      clone.addAll(list);
      return clone;
   }

   public static <T> T getLast(final List<T> list) {
      if (list.isEmpty())
         return null;
      return list.get(list.size() - 1);
   }

   public static <T> T removeLast(final List<T> list) {
      if (list.isEmpty())
         return null;
      final int idx = list.size() - 1;
      return list.remove(idx);
   }

   public static <T> void removeLast(final List<T> list, final T expectedItem) {
      if (list.isEmpty())
         return;

      final int idx = list.size() - 1;
      final T lastElement = list.remove(idx);
      if (lastElement != expectedItem)
         throw new IllegalArgumentException("Last element [" + lastElement + "] is not expected: " + expectedItem);
   }

   private CollectionUtils() {
   }
}
