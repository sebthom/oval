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
import net.sf.oval.internal.Log;

/**
 * @author Sebastian Thomschke
 */
public class ExpressionLanguageMVELImpl implements ExpressionLanguage
{
	private final static Log LOG = Log.getLog(ExpressionLanguageMVELImpl.class);

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		try
		{
			LOG.debug("Evaluating MVEL expression: {1}", expression);
			return org.mvel.MVEL.eval(expression, values);
		}
		catch (final Exception ex)
		{
			throw new ExpressionEvaluationException("Evaluating script with MVEL failed.", ex);
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
		{
			throw new ExpressionEvaluationException("The script must return a boolean value.");
		}
		return (Boolean) result;
	}
}
