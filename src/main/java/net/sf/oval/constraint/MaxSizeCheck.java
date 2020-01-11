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

import static net.sf.oval.Validator.*;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MaxSizeCheck extends AbstractAnnotationCheck<MaxSize> {
   private static final long serialVersionUID = 1L;

   private int max;

   @Override
   public void configure(final MaxSize constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMax(constraintAnnotation.value());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("max", Integer.toString(max));
      return messageVariables;
   }

   public int getMax() {
      return max;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      if (valueToValidate instanceof Collection) {
         final int size = ((Collection<?>) valueToValidate).size();
         return size <= max;
      }
      if (valueToValidate instanceof Map) {
         final int size = ((Map<?, ?>) valueToValidate).size();
         return size <= max;
      }
      if (valueToValidate.getClass().isArray()) {
         final int size = Array.getLength(valueToValidate);
         return size <= max;
      }
      return false;
   }

   public void setMax(final int max) {
      this.max = max;
      requireMessageVariablesRecreation();
   }
}
