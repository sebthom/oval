/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

/**
 * @author Sebastian Thomschke
 */
public class InvalidConfigurationException extends OValException {
   private static final long serialVersionUID = 1L;

   public InvalidConfigurationException(final String message) {
      super(message);
   }

   public InvalidConfigurationException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public InvalidConfigurationException(final Throwable cause) {
      super(cause);
   }
}
