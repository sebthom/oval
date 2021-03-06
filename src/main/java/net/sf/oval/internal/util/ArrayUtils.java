/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;

import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public final class ArrayUtils {
   public static final Object[] EMPTY_OBJECT_ARRAY = {};

   public static List<?> asList(final Object array) {
      if (array instanceof Object[]) {
         final Object[] arrayCasted = (Object[]) array;
         final List<Object> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         Collections.addAll(result, arrayCasted);
         return result;
      }
      if (array instanceof byte[]) {
         final byte[] arrayCasted = (byte[]) array;
         final List<Byte> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final byte i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof char[]) {
         final char[] arrayCasted = (char[]) array;
         final List<Character> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final char i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof short[]) {
         final short[] arrayCasted = (short[]) array;
         final List<Short> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final short i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof int[]) {
         final int[] arrayCasted = (int[]) array;
         final List<Integer> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final int i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof long[]) {
         final long[] arrayCasted = (long[]) array;
         final List<Long> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final long i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof double[]) {
         final double[] arrayCasted = (double[]) array;
         final List<Double> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final double i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof float[]) {
         final float[] arrayCasted = (float[]) array;
         final List<Float> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final float i : arrayCasted) {
            result.add(i);
         }
         return result;
      }
      if (array instanceof boolean[]) {
         final boolean[] arrayCasted = (boolean[]) array;
         final List<Boolean> result = Validator.getCollectionFactory().createList(arrayCasted.length);
         for (final boolean i : arrayCasted) {
            result.add(i);
         }
         return result;
      }

      throw new IllegalArgumentException("Argument [array] must be an array");
   }

   /**
    * In contrast to {@link java.util.Arrays#asList} this method returns a modifiable list.
    */
   public static <T> List<T> asList(final T[] array) {
      if (array == null)
         return null;
      if (array.length == 0)
         return Validator.getCollectionFactory().createList();
      final List<T> list = Validator.getCollectionFactory().createList(array.length);
      Collections.addAll(list, array);
      return list;
   }

   public static <T> Set<T> asSet(final T[] array) {
      if (array == null)
         return null;
      if (array.length == 0)
         return Validator.getCollectionFactory().createSet();
      final Set<T> set = Validator.getCollectionFactory().createSet(array.length);
      Collections.addAll(set, array);
      return set;
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

   public static void iterate(final Object array, final BiConsumer<Integer, Object> onElement) {
      if (array instanceof Object[]) {
         final Object[] arrayCasted = (Object[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof byte[]) {
         final byte[] arrayCasted = (byte[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof char[]) {
         final char[] arrayCasted = (char[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof short[]) {
         final short[] arrayCasted = (short[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof int[]) {
         final int[] arrayCasted = (int[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof long[]) {
         final long[] arrayCasted = (long[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof double[]) {
         final double[] arrayCasted = (double[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof float[]) {
         final float[] arrayCasted = (float[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }
      if (array instanceof boolean[]) {
         final boolean[] arrayCasted = (boolean[]) array;
         for (int i = 0, l = arrayCasted.length; i < l; i++) {
            onElement.accept(i, arrayCasted[i]);
         }
         return;
      }

      throw new IllegalArgumentException("Argument [array] must be an array");
   }

   private ArrayUtils() {
   }
}
