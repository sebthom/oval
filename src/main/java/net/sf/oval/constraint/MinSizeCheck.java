/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class MinSizeCheck extends AbstractAnnotationCheck<MinSize> {
   private static final long serialVersionUID = 1L;

   private int min;

   @Override
   public void configure(final MinSize constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMin(constraintAnnotation.value());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("min", Integer.toString(min));
      return messageVariables;
   }

   public int getMin() {
      return min;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      if (valueToValidate instanceof Collection) {
         final int size = ((Collection<?>) valueToValidate).size();
         return size >= min;
      }
      if (valueToValidate instanceof Map) {
         final int size = ((Map<?, ?>) valueToValidate).size();
         return size >= min;
      }
      if (valueToValidate.getClass().isArray()) {
         final int size = Array.getLength(valueToValidate);
         return size >= min;
      }
      return false;
   }

   public void setMin(final int min) {
      this.min = min;
      requireMessageVariablesRecreation();
   }
}
