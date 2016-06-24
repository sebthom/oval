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
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageBeanShellImpl extends AbstractExpressionLanguage
{
	private static final Log LOG = Log.getLog(ExpressionLanguageBeanShellImpl.class);

	public Object evaluate(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		LOG.debug("Evaluating BeanShell expression: {1}", expression);
		try
		{
			final Interpreter interpreter = new Interpreter();
			interpreter.eval("setAccessibility(true)"); // turn off access restrictions
			for (final Entry<String, ? > entry : values.entrySet())
			{
				interpreter.set(entry.getKey(), entry.getValue());
			}
			return interpreter.eval(expression);
		}
		catch (final EvalError ex)
		{
			throw new ExpressionEvaluationException("Evaluating BeanShell expression failed: " + expression, ex);
		}
	}
}