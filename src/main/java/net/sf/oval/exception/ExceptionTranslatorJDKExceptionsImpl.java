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

import net.sf.oval.ConstraintViolation;
import net.sf.oval.context.ConstructorParameterContext;
import net.sf.oval.context.FieldContext;
import net.sf.oval.context.MethodEntryContext;
import net.sf.oval.context.MethodParameterContext;
import net.sf.oval.context.MethodReturnValueContext;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * Translates OVal specific exceptions to standard exceptions part of the JRE:
 * <ul>
 * <li><code>ConstraintsViolatedException</code> for constructor/method parameter translated to <code>IllegalArgumentException</code>
 * <li><code>ConstraintsViolatedException</code> for class field translated to <code>IllegalStateException</code>
 * <li><code>ConstraintsViolatedException</code> for method return values translated to <code>IllegalStateException</code>
 * <li>Other exceptions based on <code>OValException</code> translated to <code>RuntimeException</code>
 * </ul>
 *
 * @author Sebastian Thomschke
 */
public class ExceptionTranslatorJDKExceptionsImpl implements ExceptionTranslator {
   private static final Log LOG = Log.getLog(ExceptionTranslatorJDKExceptionsImpl.class);

   @Override
   public RuntimeException translateException(final OValException ex) {
      // translate ConstraintsViolatedException based on the validation context
      if (ex instanceof ConstraintsViolatedException) {
         final ConstraintsViolatedException cex = (ConstraintsViolatedException) ex;
         final ConstraintViolation cv = cex.getConstraintViolations()[0];
         final OValContext ctx = cv.getContext();

         // translate exceptions for preconditions to IllegalArgumentExceptions
         if (ctx instanceof MethodParameterContext || ctx instanceof ConstructorParameterContext || ctx instanceof MethodEntryContext) {
            final IllegalArgumentException iaex = new IllegalArgumentException(cv.getMessage(), ex.getCause());
            iaex.setStackTrace(ex.getStackTrace());
            LOG.debug("Translated Exception {1} to {2}", ex, iaex);
            return iaex;
         }

         // translate invariant exceptions to IllegalStateExceptions
         if (ctx instanceof FieldContext || ctx instanceof MethodReturnValueContext) {
            final IllegalStateException ise = new IllegalStateException(cv.getMessage(), ex.getCause());
            ise.setStackTrace(ex.getStackTrace());
            LOG.debug("Translated Exception {1} to {2}", ex, ise);
            return ise;
         }
      }

      // translate all other messages to runtime exceptions
      {
         final RuntimeException rex = new RuntimeException(ex.getMessage(), ex.getCause());
         rex.setStackTrace(ex.getStackTrace());
         LOG.debug("Translated Exception {1} to {2}", ex, rex);
         return rex;
      }
   }
}
