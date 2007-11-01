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
public class PostCheck
{
	private static final long serialVersionUID = 1L;

	private String expression;
	private String errorCode;
	private int severity;
	private String message;
	private String language;
	private String old;
	private String[] profiles;

	public void configure(final Post constraintAnnotation)
	{
		setMessage(constraintAnnotation.message());
		setErrorCode(constraintAnnotation.errorCode());
		setSeverity(constraintAnnotation.severity());
		setExpression(constraintAnnotation.expr());
		setLanguage(constraintAnnotation.lang());
		setOld(constraintAnnotation.old());
		setProfiles(constraintAnnotation.profiles());
	}

	/**
	 * @return the failureCode
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
	 * @return the old
	 */
	public String getOld()
	{
		return old;
	}

	/**
	 * @return the profiles
	 */
	public String[] getProfiles()
	{
		return profiles;
	}

	/**
	 * @return the severity
	 */
	public int getSeverity()
	{
		return severity;
	}

	/**
	 * @param failureCode the failureCode to set
	 */
	public void setErrorCode(final String failureCode)
	{
		errorCode = failureCode;
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
	 * @param old the old to set
	 */
	public void setOld(final String old)
	{
		this.old = old;
	}

	/**
	 * @param profiles the profiles to set
	 */
	public void setProfiles(final String[] profiles)
	{
		this.profiles = profiles;
	}

	/**
	 * @param severity the severity to set
	 */
	public void setSeverity(final int severity)
	{
		this.severity = severity;
	}
}
