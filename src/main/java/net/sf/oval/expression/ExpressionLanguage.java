/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
 * Thomschke.
 *
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
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
