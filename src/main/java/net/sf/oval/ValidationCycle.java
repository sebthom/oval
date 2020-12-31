/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval;

import java.util.List;

import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.CollectionUtils;

/**
 * @author Sebastian Thomschke
 *
 * @since 3.1.0
 */
public interface ValidationCycle {

   void addConstraintViolation(ConstraintViolation violation);

   default void addConstraintViolation(final Check check, final String message, final Object invalidValue) {
      addConstraintViolation(new ConstraintViolation(check, message, getRootObject(), invalidValue, getContextPath()));
   }

   /**
    * Convenient method that returns the last element of the context path
    */
   default OValContext getContext() {
      return CollectionUtils.getLast(getContextPath());
   }

   List<OValContext> getContextPath();

   /**
    * @return the root validated object of the current validation cycle
    */
   Object getRootObject();

   Validator getValidator();
}
