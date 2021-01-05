/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertConstraintSetCheck extends AbstractAnnotationCheck<AssertConstraintSet> {
   private static final long serialVersionUID = 1L;

   private String id;

   @Override
   public void configure(final AssertConstraintSet constraintAnnotation) {
      super.configure(constraintAnnotation);
      setId(constraintAnnotation.id());
   }

   @Override
   public String getErrorCode() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   public String getId() {
      return id;
   }

   @Override
   public String getMessage() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public int getSeverity() throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   /**
    * <b>This method is not used.</b><br>
    * The validation of this special constraint is directly performed by the Validator class
    *
    * @throws UnsupportedOperationException always thrown if this method is invoked
    */
   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setErrorCode(final String errorCode) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   public void setId(final String id) {
      this.id = id;
   }

   @Override
   public void setMessage(final String message) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }

   @Override
   public void setSeverity(final int severity) throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
