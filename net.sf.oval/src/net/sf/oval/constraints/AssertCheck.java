/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005, 2006 Sebastian
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
package net.sf.oval.constraints;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertCheck extends AbstractAnnotationCheck<Assert>
{
	private static final long serialVersionUID = 1L;

	private String language = "javascript";
	private String expression;

	@Override
	public void configure(final Assert constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setExpression(constraintAnnotation.expression());
		setLanguage(constraintAnnotation.language());
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

	@Override
	public String[] getMessageValues()
	{
		return new String[]{expression};
	}

	/**
	 *  This method is not used.
	 *  The validation of this special constraint is directly performed by the Validator class
	 */
	public boolean isSatisfied(final Object validatedObject, final Object validatedValue,
			final OValContext context)
	{
		return true;
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
