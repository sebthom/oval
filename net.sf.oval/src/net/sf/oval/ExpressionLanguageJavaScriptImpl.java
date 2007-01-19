package net.sf.oval;

import java.util.Map;

import net.sf.oval.exceptions.ExpressionLanguageException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;

public class ExpressionLanguageJavaScriptImpl implements ExpressionLanguage
{
	private final Scriptable parentScope;

	public ExpressionLanguageJavaScriptImpl()
	{
		Context ctx = Context.enter();
		try
		{
			parentScope = ctx.initStandardObjects();
		}
		finally
		{
			Context.exit();
		}
	}

	public boolean evaluate(final String constraint, final Map<String, ? > values)
			throws ExpressionLanguageException
	{
		final Context ctx = Context.enter();
		try
		{
			final Scriptable scope = ctx.newObject(parentScope);
			scope.setPrototype(parentScope);
			scope.setParentScope(null);

			for (final String key : values.keySet())
			{
				final Object val = values.get(key);
				scope.put(key, scope, Context.javaToJS(val, scope));
			}
			final Object result = ctx.evaluateString(scope, constraint, "<cmd>", 1, null);
			if (!(result instanceof Boolean))
			{
				throw new ExpressionLanguageException("The script must return a boolean.");
			}
			return (Boolean) result;
		}
		catch (EvaluatorException ex)
		{
			throw new ExpressionLanguageException("Evaluating script with Rhino failed.", ex);
		}
		finally
		{
			Context.exit();
		}
	}
}
