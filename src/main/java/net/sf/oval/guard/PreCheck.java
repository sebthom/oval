/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.oval.guard;

import net.sf.oval.AbstractCheck;
import net.sf.oval.Validator;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Sebastian Thomschke
 */
public class PreCheck extends AbstractCheck
{
	private static final long serialVersionUID = 1L;

	private String expression;
	private String language;

	public void configure(final Pre constraintAnnotation)
	{
		setMessage(constraintAnnotation.message());
		setErrorCode(constraintAnnotation.errorCode());
		setSeverity(constraintAnnotation.severity());
		setExpression(constraintAnnotation.expr());
		setLanguage(constraintAnnotation.lang());
		setProfiles(constraintAnnotation.profiles());
	}

	/**
	 * @return the condition
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

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator) throws OValException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * @param condition the condition to set
	 */
	public void setExpression(final String condition)
	{
		expression = condition;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(final String language)
	{
		this.language = language;
	}
}
