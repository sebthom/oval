package net.sf.oval.expression;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.oval.exceptions.ExpressionLanguageException;

import org.codehaus.groovy.control.CompilationFailedException;

public class ExpressionLanguageGroovyImpl implements ExpressionLanguage
{
	public boolean evaluate(final String constraint, final Map<String, ? > values)
			throws ExpressionLanguageException
	{
		try
		{
			final Binding binding = new Binding();
			for (final Entry<String, ? > entry : values.entrySet())
			{
				binding.setVariable(entry.getKey(), entry.getValue());
			}
			final GroovyShell shell = new GroovyShell(binding);
			final Object result = shell.evaluate(constraint);
			if (!(result instanceof Boolean))
			{
				throw new ExpressionLanguageException("The script must return a boolean.");
			}
			return (Boolean) result;
		}
		catch (CompilationFailedException ex)
		{
			throw new ExpressionLanguageException("Evaluating script with Groovy failed.", ex);
		}
	}
}
