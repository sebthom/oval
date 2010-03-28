/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageBeanShellImpl implements ExpressionLanguage
{
	private static final Log  LOG = Log.getLog(ExpressionLanguageBeanShellImpl.class);

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		try
		{
			final Interpreter interpreter = new Interpreter();
			interpreter.eval("setAccessibility(true)"); // turn off access restrictions
			for (final Entry<String, ? > entry : values.entrySet())
			{
				interpreter.set(entry.getKey(), entry.getValue());
			}
			LOG.debug("Evaluating BeanShell expression: {1}", expression);
			return interpreter.eval(expression);
		}
		catch (final EvalError ex)
		{
			throw new ExpressionEvaluationException("Evaluating script with BeanShell failed.", ex);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean evaluateAsBoolean(final String expression, final Map<String, ? > values)
			throws ExpressionEvaluationException
	{
		final Object result = evaluate(expression, values);

		if (!(result instanceof Boolean))
			throw new ExpressionEvaluationException("The script must return a boolean value.");
		return (Boolean) result;
	}
}
