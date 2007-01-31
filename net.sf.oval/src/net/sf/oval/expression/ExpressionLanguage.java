package net.sf.oval.expression;

import java.util.Map;

import net.sf.oval.exceptions.ExpressionLanguageException;

public interface ExpressionLanguage
{
	boolean evaluate(String constraint, Map<String, ? > values) throws ExpressionLanguageException;
}
