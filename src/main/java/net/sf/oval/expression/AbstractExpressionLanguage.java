/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.expression;

import java.util.Map;

import net.sf.oval.exception.ExpressionEvaluationException;

/**
 * @author Sebastian Thomschke
 */
public abstract class AbstractExpressionLanguage implements ExpressionLanguage {

   @Override
   public boolean evaluateAsBoolean(final String expression, final Map<String, ?> values) throws ExpressionEvaluationException {
      final Object result = evaluate(expression, values);
      if (result == null)
         return false;

      if (result instanceof Boolean)
         return (Boolean) result;

      if (result instanceof Number)
         return ((Number) result).doubleValue() != 0.0;

      if (result instanceof CharSequence) {
         final CharSequence seq = (CharSequence) result;
         if (seq.length() == 0)
            return false;
         if (seq.length() == 1) {
            final char ch = seq.charAt(0);
            if (ch == '0')
               return false;
            if (ch == '1')
               return true;
         }
         switch (seq.toString().toLowerCase()) {
            case "true":
               return true;
            case "false":
               return false;
         }
      }
      throw new ExpressionEvaluationException("The script [" + expression + "] must return a boolean value but returned [" + result + "]");
   }
}
