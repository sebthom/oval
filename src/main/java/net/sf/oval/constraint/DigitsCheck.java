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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public class DigitsCheck extends AbstractAnnotationCheck<Digits> {
   private static final Log LOG = Log.getLog(DigitsCheck.class);

   private static final long serialVersionUID = 1L;

   private int maxFraction = Integer.MAX_VALUE;
   private int maxInteger = Integer.MAX_VALUE;
   private int minFraction = 0;
   private int minInteger = 0;

   @Override
   public void configure(final Digits constraintAnnotation) {
      super.configure(constraintAnnotation);
      setMinInteger(constraintAnnotation.minInteger());
      setMaxInteger(constraintAnnotation.maxInteger());
      setMinFraction(constraintAnnotation.minFraction());
      setMaxFraction(constraintAnnotation.maxFraction());
   }

   @Override
   protected Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("maxInteger", Integer.toString(maxInteger));
      messageVariables.put("minInteger", Integer.toString(minInteger));
      messageVariables.put("maxFraction", Integer.toString(maxFraction));
      messageVariables.put("minFraction", Integer.toString(minFraction));
      return messageVariables;
   }

   @Override
   protected ConstraintTarget[] getAppliesToDefault() {
      return new ConstraintTarget[] {ConstraintTarget.VALUES};
   }

   public int getMaxFraction() {
      return maxFraction;
   }

   public int getMaxInteger() {
      return maxInteger;
   }

   public int getMinFraction() {
      return minFraction;
   }

   public int getMinInteger() {
      return minInteger;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context, final Validator validator) {
      if (valueToValidate == null)
         return true;

      final int fractLen, intLen;
      if (valueToValidate instanceof Integer) {
         final int value = (Integer) valueToValidate;
         intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
         fractLen = 0;
      } else if (valueToValidate instanceof Long) {
         final long value = (Long) valueToValidate;
         intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
         fractLen = 0;
      } else if (valueToValidate instanceof Short) {
         final short value = (Short) valueToValidate;
         intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
         fractLen = 0;
      } else if (valueToValidate instanceof Byte) {
         final byte value = (Byte) valueToValidate;
         intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
         fractLen = 0;
      } else if (valueToValidate instanceof BigInteger) {
         final long value = ((BigInteger) valueToValidate).longValue();
         intLen = value == 0 ? 1 : (int) Math.log10(value) + 1;
         fractLen = 0;
      } else {
         BigDecimal value = null;
         if (valueToValidate instanceof BigDecimal) {
            value = (BigDecimal) valueToValidate;
         } else {
            try {
               value = new BigDecimal(valueToValidate.toString());
            } catch (final NumberFormatException ex) {
               LOG.debug("Failed to parse numeric value: " + valueToValidate, ex);
               return false;
            }
         }
         final int valueScale = value.scale();
         final long longValue = value.longValue();
         intLen = longValue == 0 ? 1 : (int) Math.log10(longValue) + 1;
         fractLen = valueScale > 0 ? valueScale : 0;
      }

      return intLen <= maxInteger && intLen >= minInteger && fractLen <= maxFraction && fractLen >= minFraction;
   }

   public void setMaxFraction(final int maxFraction) {
      this.maxFraction = maxFraction;
      requireMessageVariablesRecreation();
   }

   public void setMaxInteger(final int maxInteger) {
      this.maxInteger = maxInteger;
      requireMessageVariablesRecreation();
   }

   public void setMinFraction(final int minFraction) {
      this.minFraction = minFraction;
      requireMessageVariablesRecreation();
   }

   public void setMinInteger(final int minInteger) {
      this.minInteger = minInteger;
      requireMessageVariablesRecreation();
   }
}
