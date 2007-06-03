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
package net.sf.oval.constraint;

import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.ExpressionEvaluationException;
import net.sf.oval.exception.ExpressionLanguageNotAvailableException;
import net.sf.oval.expression.ExpressionLanguage;
import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class AssertCheck extends AbstractAnnotationCheck<Assert>
{
	private static final long serialVersionUID = 1L;

	private String language;
	private String expression;

	@Override
	public void configure(final Assert constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setExpression(constraintAnnotation.expr());
		setLanguage(constraintAnnotation.lang());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory().createMap(2);
		messageVariables.put("expression", expression);
		messageVariables.put("language", language);
		return messageVariables;
	}

	/**
	 * @return the expression
	 */
	public String getExpression()
	{
		return expression;
	}

	/**
	 * @return the language
	 */
	public String getLanguage()
	{
		return language;
	}

	public boolean isSatisfied(final Object validatedObject, final Object validatedValue,
			final OValContext context, final Validator validator) throws ExpressionEvaluationException, ExpressionLanguageNotAvailableException
	{
		Map<String, Object> values = CollectionFactoryHolder.getFactory().createMap();
		values.put("_value", validatedValue);
		values.put("_this", validatedObject);
		
		final ExpressionLanguage el = validator.getExpressionLanguage(language);
		return el.evaluateAsBoolean(expression, values);
	}

	/**
	 * @param expression the expression to set
	 */
	public void setExpression(final String expression)
	{
		this.expression = expression;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(final String language)
	{
		this.language = language;
	}

}
