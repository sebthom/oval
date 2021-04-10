/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

import java.util.Collection;
import java.util.List;

import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 */
public final class StringUtils {

   public static boolean isBlank(final String str) {
      if (isEmpty(str))
         return true;

      for (int i = 0, l = str.length(); i < l; i++) {
         if (!Character.isWhitespace(str.charAt(i)))
            return false;
      }
      return true;
   }

   public static boolean isEmpty(final String str) {
      return str == null || str.length() == 0;
   }

   public static String join(final Collection<?> values, final char delimiter) {
      if (values == null || values.isEmpty())
         return "";

      final StringBuilder out = new StringBuilder(3 * values.size());
      boolean isFirst = true;
      for (final Object value : values) {
         if (isFirst) {
            isFirst = false;
         } else {
            out.append(delimiter);
         }
         out.append(value);
      }
      return out.toString();
   }

   public static String join(final Object[] values, final char delimiter) {
      if (values == null || values.length == 0)
         return "";

      final StringBuilder out = new StringBuilder(3 * values.length);
      for (int i = 0, l = values.length; i < l; i++) {
         if (i > 0) {
            out.append(delimiter);
         }
         out.append(values[i]);
      }
      return out.toString();
   }

   /**
    * high-performance case-sensitive string replacement
    */
   public static String replaceAll(final String searchIn, final String searchFor, final String replaceWith) {
      final StringBuilder out = new StringBuilder();

      int startAt = 0, foundAt = 0;
      final int searchForLength = searchFor.length();

      while ((foundAt = searchIn.indexOf(searchFor, startAt)) >= 0) {
         out.append(searchIn.substring(startAt, foundAt)).append(replaceWith);
         startAt = foundAt + searchForLength;
      }

      return out.append(searchIn.substring(startAt, searchIn.length())).toString();
   }

   public static List<String> split(final String str, final char separator, final int maxParts) {
      final List<String> result = Validator.getCollectionFactory().createList();
      int startAt = 0;
      while (true) {
         final int foundAt = str.indexOf(separator, startAt);
         if (foundAt == -1 || result.size() == maxParts - 1) {
            result.add(str.substring(startAt, str.length()));
            break;
         }
         result.add(str.substring(startAt, foundAt));
         startAt = foundAt + 1;
      }
      return result;
   }

   public static String substringBeforeLast(final String str, final char delimiter) {
      final int pos = str.lastIndexOf(delimiter);
      if (pos == -1)
         return str;
      return str.substring(0, pos);
   }

   private StringUtils() {
   }
}
