/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import java.math.BigDecimal;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class NotNegativeCheck extends AbstractAnnotationCheck<NotNegative> {
   private static final BigDecimal ZERO = BigDecimal.valueOf(0);

   private static final long serialVersionUID = 1L;

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      if (valueToValidate instanceof Number) {
         if (valueToValidate instanceof Float || valueToValidate instanceof Double)
            return ((Number) valueToValidate).doubleValue() >= 0;
         if (valueToValidate instanceof BigDecimal)
            return ((BigDecimal) valueToValidate).compareTo(ZERO) >= 0;
         return ((Number) valueToValidate).longValue() >= 0;
      }

      final String stringValue = valueToValidate.toString();
      try {
         return Double.parseDouble(stringValue) >= 0;
      } catch (final NumberFormatException e) {
         return false;
      }
   }
}
