/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.constraint;

import java.lang.annotation.Annotation;
import java.util.List;

import net.sf.oval.Check;
import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * Internal check holding multiple check instances of the same type, e.g. used for @Assert.List(...)
 *
 * @author Sebastian Thomschke
 */
public class ConstraintsCheck extends AbstractAnnotationCheck<Annotation> {
   private static final long serialVersionUID = 1L;

   public List<Check> checks;

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.CONTAINER /*, ConstraintTarget.KEYS, ConstraintTarget.VALUES,
                                                                ConstraintTarget.RECURSIVE*/ };
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
