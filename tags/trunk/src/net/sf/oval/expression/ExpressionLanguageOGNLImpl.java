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
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import ognl.Ognl;
import ognl.OgnlContext;
import ognl.OgnlException;

/**
 * @author Sebastian Thomschke
 *
 */
public class ExpressionLanguageOGNLImpl implements ExpressionLanguage
{
	private final static Log LOG = Log.getLog(ExpressionLanguageOGNLImpl.class);

	public Object evaluate(final String expression, final Map<String, ? > values)
			throws ExpressionEvaluationException
	{
		try
		{
			final OgnlContext ctx = (OgnlContext) Ognl.createDefaultContext(null);

			for (final Entry<String, ? > entry : values.entrySet())
			{
				ctx.put(entry.getKey(), entry.getValue());
			}

			LOG.debug("Evaluating OGNL expression: {}", expression);
			return Ognl.getValue(expression, ctx);
		}
		catch (final OgnlException ex)
		{
			throw new ExpressionEvaluationException("Evaluating script with OGNL failed.", ex);
		}
	}

	public boolean evaluateAsBoolean(final String expression, final Map<String, ? > values)
			throws ExpressionEvaluationException
	{
		final Object result = evaluate(expression, values);

		if (!(result instanceof Boolean))
			throw new ExpressionEvaluationException("The script must return a boolean value.");
		return (Boolean) result;
	}
}
