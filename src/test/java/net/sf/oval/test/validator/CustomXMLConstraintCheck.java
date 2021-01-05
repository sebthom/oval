/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.test.validator;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.configuration.annotation.Constraint;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class CustomXMLConstraintCheck extends AbstractAnnotationCheck<Constraint> {
   private static final long serialVersionUID = 1L;

   @Override
   public String getMessage() {
      return "Value must have more than 4 characters!";
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;
      return valueToValidate.toString().length() > 4;
   }
}
