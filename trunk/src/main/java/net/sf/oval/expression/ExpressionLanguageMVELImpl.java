/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2015 Sebastian
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

import org.mvel2.MVEL;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageMVELImpl extends AbstractExpressionLanguage
{
	private static final Log LOG = Log.getLog(ExpressionLanguageMVELImpl.class);

	private final ObjectCache<String, Object> expressionCache = new ObjectCache<String, Object>();

	public Object evaluate(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		LOG.debug("Evaluating MVEL expression: {1}", expression);
		try
		{
			Object expr = expressionCache.get(expression);
			if (expr == null)
			{
				expr = MVEL.compileExpression(expression);
				expressionCache.put(expression, expr);
			}
			return MVEL.executeExpression(expr, values);
		}
		catch (final Exception ex)
		{
			throw new ExpressionEvaluationException("Evaluating MVEL expression failed: " + expression, ex);
		}
	}
}