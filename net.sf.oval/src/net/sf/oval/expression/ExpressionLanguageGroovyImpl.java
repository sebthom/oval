/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2007 Sebastian
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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;

import org.codehaus.groovy.control.CompilationFailedException;

/**
 * @author Sebastian Thomschke
 *
 */
public class ExpressionLanguageGroovyImpl implements ExpressionLanguage
{
	public Object evaluate(final String expression, final Map<String, ? > values)
			throws ExpressionEvaluationException
	{
		try
		{
			final Binding binding = new Binding();
			for (final Entry<String, ? > entry : values.entrySet())
			{
				binding.setVariable(entry.getKey(), entry.getValue());
			}
			final GroovyShell shell = new GroovyShell(binding);
			return shell.evaluate(expression);
		}
		catch (final CompilationFailedException ex)
		{
			throw new ExpressionEvaluationException("Evaluating script with Groovy failed.", ex);
		}
	}

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
