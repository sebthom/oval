/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.internal.util;

import java.util.Collection;

/**
 * @author Sebastian Thomschke
 */
public final class Assert {
   private static RuntimeException _adjustStacktrace(final RuntimeException ex) {
      final StackTraceElement[] stack = ex.getStackTrace();
      final StackTraceElement[] newStack = new StackTraceElement[stack.length - 1];
      System.arraycopy(stack, 1, newStack, 0, stack.length - 1);
      ex.setStackTrace(newStack);
      return ex;
   }

   public static void argumentNotBlank(final String name, final String value) throws IllegalArgumentException {
      if (value == null)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
      if (value.length() == 0)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be empty"));
      if (StringUtils.isBlank(value))
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be blank"));
   }

   public static <T> void argumentNotEmpty(final String name, final Collection<T> value) throws IllegalArgumentException {
      if (value == null)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
      if (value.isEmpty())
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be empty"));
   }

   public static <T> void argumentNotEmpty(final String name, final T[] value) throws IllegalArgumentException {
      if (value == null)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
      if (value.length == 0)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be empty"));
   }

   public static void argumentNotNull(final String name, final Object value) throws IllegalArgumentException {
      if (value == null)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
   }

   private Assert() {
   }
}
