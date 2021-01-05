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
public class NotBlankCheck extends AbstractAnnotationCheck<NotBlank> {
   private static final long serialVersionUID = 1L;

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) {
      if (valueToValidate == null)
         return true;

      final String str = valueToValidate.toString();

      final int l = str.length();
      for (int i = 0; i < l; i++) {
         final char ch = str.charAt(i);
         if (!Character.isSpaceChar(ch) || !Character.isWhitespace(ch))
            return true;
      }

      return false;
   }
}
