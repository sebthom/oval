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

	private String expression;
	private String errorCode;
	private String language;
	private String message;
	private int priority;
	private String[] profiles;

	public void configure(final Pre constraintAnnotation)
	{
		setMessage(constraintAnnotation.message());
		setErrorCode(constraintAnnotation.errorCode());
		setPriority(constraintAnnotation.priority());
		setExpression(constraintAnnotation.expr());
		setLanguage(constraintAnnotation.lang());
		setProfiles(constraintAnnotation.profiles());
	}

	/**
	 * @return the errorCode
	 */
	public String getErrorCode()
	{
		return errorCode;
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
	 * @return the priority
	 */
	public int getPriority()
	{
		return priority;
	}

	/**
	 * @return the profiles
	 */
	public String[] getProfiles()
	{
		return profiles;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(final String errorCode)
	{
		this.errorCode = errorCode;
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

	/**
	 * @param message the message to set
	 */
	public void setMessage(final String message)
	{
		this.message = message;
	}

	/**
	 * @param priority the priority to set
	 */
	public void setPriority(final int priority)
	{
		this.priority = priority;
	}

	/**
	 * @param profiles the profiles to set
	 */
	public void setProfiles(final String[] profiles)
	{
		this.profiles = profiles;
	}
}
