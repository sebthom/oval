/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
