/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.exception;

/**
 * The root exception of all custom exceptions thrown by OVal
 * 
 * @author Sebastian Thomschke
 */
public class OValException extends RuntimeException {
   private static final long serialVersionUID = 1L;

   public OValException(final String message) {
      super(message);
   }

   public OValException(final String message, final Throwable cause) {
      super(message, cause);
   }

   public OValException(final Throwable cause) {
      super(cause);
   }
}
