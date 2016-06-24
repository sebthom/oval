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
public abstract class AbstractExpressionLanguage implements ExpressionLanguage
{

	public boolean evaluateAsBoolean(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		final Object result = evaluate(expression, values);
		if (result == null) return false;
		if (result instanceof Boolean) return (Boolean) result;
		if (result instanceof Number) return ((Number) result).doubleValue() != 0.0;
		if (result instanceof CharSequence)
		{
			final CharSequence seq = (CharSequence) result;
			if (seq.length() == 0) return false;
			if (seq.length() == 1)
			{
				final char ch = seq.charAt(0);
				if (ch == '0') return false;
				if (ch == '1') return true;
			}
			final String str = seq.toString().toLowerCase();
			if (str.equals("true")) return true;
			if (str.equals("false")) return true;
		}
		throw new ExpressionEvaluationException("The script [" + expression + "] must return a boolean value but returned [" + result + "]");
	}
}