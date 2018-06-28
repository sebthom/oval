/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertValidCheck extends AbstractAnnotationCheck<AssertValid> {
   private static final long serialVersionUID = 1L;

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.CONTAINER, ConstraintTarget.VALUES};
   }

   /**
    * <b>This method is not used.</b><br>
    * The validation of this special constraint is directly performed by the Validator class
    *
    * @throws UnsupportedOperationException always thrown if this method is invoked
    */
   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator)
      throws UnsupportedOperationException {
      throw new UnsupportedOperationException();
   }
}
