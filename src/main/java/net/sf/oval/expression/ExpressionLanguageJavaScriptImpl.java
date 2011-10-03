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
import java.util.Map.Entry;

import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.internal.Log;
import net.sf.oval.internal.util.ObjectCache;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;

/**
 * @author Sebastian Thomschke
 *
 */
public class ExpressionLanguageJavaScriptImpl implements ExpressionLanguage
{
	private static final Log LOG = Log.getLog(ExpressionLanguageJavaScriptImpl.class);

	private final Scriptable parentScope;

	private final ObjectCache<String, Script> scriptCache = new ObjectCache<String, Script>();

	/**
	 * Default constructor.
	 */
	public ExpressionLanguageJavaScriptImpl()
	{
		final Context ctx = ContextFactory.getGlobal().enterContext();
		try
		{
			parentScope = ctx.initStandardObjects();
		}
		finally
		{
			Context.exit();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public Object evaluate(final String expression, final Map<String, ? > values) throws ExpressionEvaluationException
	{
		final Context ctx = ContextFactory.getGlobal().enterContext();
		try
		{
			Script script = scriptCache.get(expression);
			if (script == null)
			{
				ctx.setOptimizationLevel(9);
				script = ctx.compileString(expression, "<cmd>", 1, null);
				scriptCache.put(expression, script);
			}

			final Scriptable scope = ctx.newObject(parentScope);
			scope.setPrototype(parentScope);
			scope.setParentScope(null);

			for (final Entry<String, ? > entry : values.entrySet())
			{
				scope.put(entry.getKey(), scope, Context.javaToJS(entry.getValue(), scope));
			}

			LOG.debug("Evaluating JavaScript expression: {1}", expression);
			return script.exec(ctx, scope);
		}
		catch (final EvaluatorException ex)
		{
			throw new ExpressionEvaluationException("Evaluating script with Rhino failed.", ex);
		}
		finally
		{
			Context.exit();
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
