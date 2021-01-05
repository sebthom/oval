/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class AssertValidCheck extends AbstractAnnotationCheck<AssertValid> {
   private static final long serialVersionUID = 1L;

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES, ConstraintTarget.RECURSIVE};
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
}
