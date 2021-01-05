/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

/**
 * @author Sebastian Thomschke
 */
public class FieldNotFoundException extends ReflectionException {
   private static final long serialVersionUID = 1L;

   public FieldNotFoundException(final String message) {
      super(message);
   }

   public FieldNotFoundException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public FieldNotFoundException(final Throwable cause) {
      super(cause);
   }
}
