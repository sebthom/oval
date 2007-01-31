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
package net.sf.oval.checks;

import java.util.Map;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.constraints.Contains;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class ContainsCheck extends AbstractAnnotationCheck<Contains>
{
	private static final long serialVersionUID = 1L;

	private boolean ignoreCase;

	private String substring;
	private String substringLowerCase;

	@Override
	public void configure(final Contains constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setIgnoreCase(constraintAnnotation.ignoreCase());
		setSubstring(constraintAnnotation.substring());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactory.INSTANCE.createMap(2);
		messageVariables.put("ignoreCase", Boolean.toString(ignoreCase));
		messageVariables.put("substring", substring);
		return messageVariables;
	}

	/**
	 * @return the substring
	 */
	public String getSubstring()
	{
		return substring;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		if (ignoreCase) return value.toString().toLowerCase().indexOf(substringLowerCase) > -1;

		return value.toString().indexOf(substring) > -1;
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(final boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
	}

	/**
	 * @param substring the substring to set
	 */
	public void setSubstring(final String substring)
	{
		this.substring = substring;
		substringLowerCase = substring.toLowerCase();
	}
}
