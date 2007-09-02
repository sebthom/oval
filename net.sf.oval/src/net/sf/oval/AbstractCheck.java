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
package net.sf.oval;

import java.util.Map;

/**
 * Partial implementation of check classes.
 * 
 * @author Sebastian Thomschke
 */
public abstract class AbstractCheck implements Check
{
	protected String message;

	protected String[] profiles;

	public String getMessage()
	{
		/*
		 * if the message has not been initialized (which might be the case when using XML configuration),
		 * construct the string based on this class' name minus the appendix "Check" plus the appendix ".violated"
		 */
		if (message == null)
		{
			final String className = getClass().getName();
			if (className.endsWith("Check"))
				message = className.substring(0, getClass().getName().length() - 5) + ".violated";
			else
				message = className + ".violated";
		}
		return message;
	}

	public Map<String, String> getMessageVariables()
	{
		return null;
	}

	public String[] getProfiles()
	{
		return profiles;
	}

	public void setMessage(final String message)
	{
		this.message = message;
	}

	public void setProfiles(final String... profiles)
	{
		this.profiles = profiles;
	}
}
