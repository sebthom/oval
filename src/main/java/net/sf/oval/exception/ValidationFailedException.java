/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

/**
 * @author Sebastian Thomschke
 */
public class ValidationFailedException extends OValException {
   private static final long serialVersionUID = 1L;

   public ValidationFailedException(final String message) {
      super(message);
   }

   public ValidationFailedException(final String message, final Throwable cause) {
      super(message, cause);
   }
}
