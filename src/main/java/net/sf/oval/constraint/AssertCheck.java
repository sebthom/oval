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

import java.util.Map;

import net.sf.oval.ValidationCycle;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.exception.ExpressionLanguageNotAvailableException;
import net.sf.oval.expression.ExpressionLanguage;

/**
 * @author Sebastian Thomschke
 */
public class AssertCheck extends AbstractAnnotationCheck<Assert> {
   private static final long serialVersionUID = 1L;

   private String expr;
   private String lang;

   @Override
   public void configure(final Assert constraintAnnotation) {
      super.configure(constraintAnnotation);
      setExpr(constraintAnnotation.expr());
      setLang(constraintAnnotation.lang());
   }

   @Override
   public Map<String, String> createMessageVariables() {
      final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
      messageVariables.put("expression", expr);
      messageVariables.put("language", lang);
      return messageVariables;
   }

   /**
    * @return the expression
    */
   public String getExpr() {
      return expr;
   }

   /**
    * @return the expression language
    */
   public String getLang() {
      return lang;
   }

   @Override
   public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final ValidationCycle cycle) throws ExpressionEvaluationException,
      ExpressionLanguageNotAvailableException {
      final Map<String, Object> values = getCollectionFactory().createMap();
      values.put("_value", valueToValidate);
      values.put("_this", validatedObject);

      final ExpressionLanguage el = cycle.getValidator().getExpressionLanguageRegistry().getExpressionLanguage(lang);
      return el.evaluateAsBoolean(expr, values);
   }

   public void setExpr(final String expression) {
      expr = expression;
      requireMessageVariablesRecreation();
   }

   public void setLang(final String language) {
      lang = language;
      requireMessageVariablesRecreation();
   }
}
