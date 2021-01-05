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
public interface ExpressionLanguage {
   /**
    * Evaluates the given expression.
    * 
    * @param expression the expression to evaluate
    * @param values context values passed to the interpreter
    * @return the result of the expression evaluation
    * @throws ExpressionEvaluationException in case of an invalid expression
    */
   Object evaluate(String expression, Map<String, ?> values) throws ExpressionEvaluationException;

   /**
    * Evaluates the given expression and expects it to return a boolean.
    * <li><code>null</code> is interpreted as <code>false</code>
    * <li>a number with value 0 is interpreted as <code>false</code>
    * <li>an empty {@link CharSequence} is interpreted as <code>false</code>
    * <li>a {@link CharSequence} with value "0" is interpreted as <code>false</code>
    * <li>a {@link CharSequence} with value "1" is interpreted as <code>true</code>
    * <li>a {@link CharSequence} with value "false" is case-insensitively interpreted as <code>false</code>
    * <li>a {@link CharSequence} with value "true" is case-insensitively interpreted as <code>true</code>
    *
    * @param expression the expression to evaluate
    * @param values context values passed to the interpreter
    * @return the result of the expression evaluation
    * @throws ExpressionEvaluationException If an error during evaluation occurs or if the return value is not a boolean value.
    */
   boolean evaluateAsBoolean(String expression, Map<String, ?> values) throws ExpressionEvaluationException;
}
