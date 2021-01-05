/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

/**
 * @author Sebastian Thomschke
 */
public class MethodNotFoundException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   public MethodNotFoundException(final String message) {
      super(message);
   }

   public MethodNotFoundException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public MethodNotFoundException(final Throwable cause) {
      super(cause);
   }
}
