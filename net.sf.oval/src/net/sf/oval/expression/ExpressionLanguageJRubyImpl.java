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

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;

import org.jruby.Ruby;
import org.jruby.javasupport.JavaEmbedUtils;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * @author Sebastian Thomschke
 *
 */
public class ExpressionLanguageJRubyImpl implements ExpressionLanguage
{
	public Object evaluate(final String expression, final Map<String, ? > values)
			throws ExpressionEvaluationException
	{
		try
		{
			final Ruby runtime = JavaEmbedUtils.initialize(new ArrayList<String>());

			StringBuilder localVars = new StringBuilder();
			for (final Entry<String, ? > entry : values.entrySet())
			{
				runtime.getGlobalVariables().set("$" + entry.getKey(),
						JavaEmbedUtils.javaToRuby(runtime, entry.getValue()));
				localVars.append(entry.getKey());
				localVars.append("=$");
				localVars.append(entry.getKey());
				localVars.append("\n");

			}
			final IRubyObject result = runtime.evalScript(localVars + expression);
			return JavaEmbedUtils.rubyToJava(runtime, result, Object.class);
		}
		catch (final RuntimeException ex)
		{
			ex.printStackTrace(System.out);
			throw new ExpressionEvaluationException("Evaluating script with JRuby failed.", ex);
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
