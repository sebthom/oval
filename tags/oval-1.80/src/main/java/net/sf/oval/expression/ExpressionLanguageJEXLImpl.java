/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
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
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;

import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageJEXLImpl implements ExpressionLanguage
{
	private static final Log LOG = Log.getLog(ExpressionLanguageJEXLImpl.class);

	private static final JexlEngine jexl = new JexlEngine();

	private final ObjectCache<String, Expression> expressionCache = new ObjectCache<String, Expression>();

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		try
		{
			Expression expr = expressionCache.get(expression);
			if (expr == null)
			{
				expr = jexl.createExpression(expression);
				expressionCache.put(expression, expr);
			}

			@SuppressWarnings("unchecked")
			final JexlContext ctx = new MapContext((Map<String, Object>) values);

			LOG.debug("Evaluating JEXL expression: {1}", expression);
			return expr.evaluate(ctx);
		}
		catch (final Exception ex)
		{
			throw new ExpressionEvaluationException("Evaluating script with JEXL failed.", ex);
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
