/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.exception;

/**
 * An exception translator for all exception thrown by the guard
 * during runtime when performing constraint validations on guarded objects.
 *
 * @author Sebastian Thomschke
 */
public interface ExceptionTranslator {
   /**
    * If this method returns null, the original exception is thrown.
    *
    * @return the exception to throw
    */
   RuntimeException translateException(OValException ex);
}
