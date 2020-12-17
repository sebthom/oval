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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Sebastian Thomschke
 */
public final class ArrayUtils {
   public static final Object[] EMPTY_OBJECT_ARRAY = {};

   /**
    * @throws IllegalArgumentException if <code>collection == null</code>
    */
   @SafeVarargs
   public static <T> int addAll(final Collection<T> collection, final T... elements) throws IllegalArgumentException {
      if (elements == null)
         return 0;

      int count = 0;
      for (final T elem : elements)
         if (collection.add(elem)) {
            count++;
         }
      return count;
   }

   public static List<?> asList(final Object array) {
      if (array instanceof Object[]) {
         final Object[] arrayCasted = (Object[]) array;
         final List<Object> result = new ArrayList<>(arrayCasted.length);
         Collections.addAll(result, arrayCasted);
         return result;
      }
      if (array instanceof byte[]) {
         final byte[] arrayCasted = (byte[]) array;
         final List<Byte> result = new ArrayList<>(arrayCasted.length);
         for (final byte i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof char[]) {
         final char[] arrayCasted = (char[]) array;
         final List<Character> result = new ArrayList<>(arrayCasted.length);
         for (final char i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof short[]) {
         final short[] arrayCasted = (short[]) array;
         final List<Short> result = new ArrayList<>(arrayCasted.length);
         for (final short i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof int[]) {
         final int[] arrayCasted = (int[]) array;
         final List<Integer> result = new ArrayList<>(arrayCasted.length);
         for (final int i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof long[]) {
         final long[] arrayCasted = (long[]) array;
         final List<Long> result = new ArrayList<>(arrayCasted.length);
         for (final long i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof double[]) {
         final double[] arrayCasted = (double[]) array;
         final List<Double> result = new ArrayList<>(arrayCasted.length);
         for (final double i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof float[]) {
         final float[] arrayCasted = (float[]) array;
         final List<Float> result = new ArrayList<>(arrayCasted.length);
         for (final float i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof boolean[]) {
         final boolean[] arrayCasted = (boolean[]) array;
         final List<Boolean> result = new ArrayList<>(arrayCasted.length);
         for (final boolean i : arrayCasted) {
            result.add(i);
         }
         return result;
      }

      throw new IllegalArgumentException("Argument [array] must be an array");
   }

   public static <T> List<T> asList(final T[] array) {
      final List<T> result = new ArrayList<>(array.length);
      Collections.addAll(result, array);
      return result;
   }

   public static <T> Set<T> asSet(final T[] array) {
      final Set<T> result = new HashSet<>(array.length);
      Collections.addAll(result, array);
      return result;
   }

   public static <T> boolean containsEqual(final T[] theArray, final T theItem) {
      for (final T t : theArray) {
         if (t == theItem)
            return true;
         if (t != null && t.equals(theItem))
            return true;
      }
      return false;
   }

   public static <T> boolean containsSame(final T[] theArray, final T theItem) {
      for (final T t : theArray)
         if (t == theItem)
            return true;
      return false;
   }

   private ArrayUtils() {
   }
}
