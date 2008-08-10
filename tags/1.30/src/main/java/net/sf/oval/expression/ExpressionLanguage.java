/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
 *
 */
public interface ExpressionLanguage
{
	/**
	 * Evaluates the given expression.
	 * @param expression the expression to evaluate
	 * @param values context values passed to the interpreter
	 * @return the result of the expression evaluation
	 * @throws ExpressionEvaluationException in case of an invalid expression
	 */
	Object evaluate(String expression, Map<String, ? > values) throws ExpressionEvaluationException;

	/**
	 * Evaluates the given expression and expects it to return a boolean.
	 * @param expression the expression to evaluate
	 * @param values context values passed to the interpreter
	 * @return the result of the expression evaluation 
	 * @throws ExpressionEvaluationException If an error during evaluation occurs or if the return value is not a boolean value.
	 */
	boolean evaluateAsBoolean(String expression, Map<String, ? > values) throws ExpressionEvaluationException;
}
