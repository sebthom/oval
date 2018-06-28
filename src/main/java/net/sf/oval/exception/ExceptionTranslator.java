/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
