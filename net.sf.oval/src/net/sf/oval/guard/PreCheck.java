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
package net.sf.oval.guard;


/**
 * @author Sebastian Thomschke
 */
public class PreCheck
{
	private static final long serialVersionUID = 1L;

	private String language;
	private String expression;
	private String message;
	private String[] profiles;

	public void configure(final Pre constraintAnnotation)
	{
		setMessage(constraintAnnotation.message());
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
	 * @return the message
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @return the profiles
	 */
	public String[] getProfiles()
	{
		return profiles;
	}

	/**
	 * @param condition the condition to set
	 */
	public void setExpression(final String condition)
	{
		this.expression = condition;
	}

	/**
	 * @param language the language to set
	 */
	public void setLanguage(final String language)
	{
		this.language = language;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * @param profiles the profiles to set
	 */
	public void setProfiles(final String[] profiles)
	{
		this.profiles = profiles;
	}
}
