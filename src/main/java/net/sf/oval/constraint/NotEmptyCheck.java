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

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotEmptyCheck extends AbstractAnnotationCheck<NotEmpty> {
   private static final long serialVersionUID = 1L;

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      if (valueToValidate instanceof Collection)
         return !((Collection<?>) valueToValidate).isEmpty();

      if (valueToValidate instanceof Map)
         return !((Map<?, ?>) valueToValidate).isEmpty();

      if (valueToValidate.getClass().isArray())
         return Array.getLength(valueToValidate) > 0;

      return valueToValidate.toString().length() > 0;
   }
}
