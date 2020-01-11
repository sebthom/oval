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
public final class Assert {
   private static RuntimeException _adjustStacktrace(final RuntimeException ex) {
      final StackTraceElement[] stack = ex.getStackTrace();
      final StackTraceElement[] newStack = new StackTraceElement[stack.length - 1];
      System.arraycopy(stack, 1, newStack, 0, stack.length - 1);
      ex.setStackTrace(newStack);
      return ex;
   }

   public static <T> void argumentNotEmpty(final String name, final String value) throws IllegalArgumentException {
      if (value == null)
         throw _adjustStacktrace(new IllegalArgumentException("[" + name + "] must not be null"));
      if (value.length() == 0)
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
