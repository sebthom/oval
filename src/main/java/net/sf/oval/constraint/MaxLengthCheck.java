/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.constraint;

import static net.sf.oval.Validator.*;

import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;

/**
 * @author Sebastian Thomschke
 */
public class MaxLengthCheck extends AbstractAnnotationCheck<MaxLength> {
   private static final long serialVersionUID = 1L;

   private int max;

   @Override
   public void configure(final MaxLength constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMax(constraintAnnotation.value());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("max", Integer.toString(max));
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public int getMax() {
      return max;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      final int len = valueToValidate.toString().length();
      return len <= max;
   }

   public void setMax(final int max) {
      this.max = max;
      requireMessageVariablesRecreation();
   }
}
