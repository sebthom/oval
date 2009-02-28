/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval.constraint;

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.exception.ExpressionLanguageNotAvailableException;
import net.sf.oval.expression.ExpressionLanguage;

/**
 * @author Sebastian Thomschke
 */
public class AssertCheck extends AbstractAnnotationCheck<Assert>
{
	private static final long serialVersionUID = 1L;

	private String lang;
	private String expr;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final Assert constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setExpr(constraintAnnotation.expr());
		setLang(constraintAnnotation.lang());
	}

	/**
	 * @return the expression
	 */
	public String getExpr()
	{
		return expr;
	}

	/**
	 * @return the expression language
	 */
	public String getLang()
	{
		return lang;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator) throws ExpressionEvaluationException, ExpressionLanguageNotAvailableException
	{
		final Map<String, Object> values = getCollectionFactory().createMap();
		values.put("_value", valueToValidate);
		values.put("_this", validatedObject);

		final ExpressionLanguage el = validator.getExpressionLanguage(lang);
		return el.evaluateAsBoolean(expr, values);
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpr(final String expression)
	{
		expr = expression;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param language the expression language to set
	 */
	public void setLang(final String language)
	{
		lang = language;
		requireMessageVariablesRecreation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> createMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
		messageVariables.put("expression", expr);
		messageVariables.put("language", lang);
		return messageVariables;
	}

}
