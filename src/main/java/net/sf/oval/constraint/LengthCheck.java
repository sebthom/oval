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

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class LengthCheck extends AbstractAnnotationCheck<Length> {
   private static final long serialVersionUID = 1L;

   private int min;
   private int max;

   @Override
   public void configure(final Length constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMax(constraintAnnotation.max());
      setMin(constraintAnnotation.min());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("max", Integer.toString(max));
      messageVariables.put("min", Integer.toString(min));
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public int getMax() {
      return max;
   }

   public int getMin() {
      return min;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      final int len = valueToValidate.toString().length();
      return len >= min && len <= max;
   }

   public void setMax(final int max) {
      this.max = max;
      requireMessageVariablesRecreation();
   }

   public void setMin(final int min) {
      this.min = min;
      requireMessageVariablesRecreation();
   }
}
