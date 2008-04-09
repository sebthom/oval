/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2008 Sebastian
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
import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class HasSubstringCheck extends AbstractAnnotationCheck<HasSubstring>
{
	private static final long serialVersionUID = 1L;

	private boolean ignoreCase;

	private String substring;
	private transient String substringLowerCase;

	@Override
	public void configure(final HasSubstring constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setIgnoreCase(constraintAnnotation.ignoreCase());
		setSubstring(constraintAnnotation.substring());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
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

	private String getSubstringLowerCase()
	{
		if (substringLowerCase == null && substring != null)
			substringLowerCase = substring.toLowerCase();
		return substringLowerCase;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator)
	{
		if (valueToValidate == null) return true;

		if (ignoreCase)
			return valueToValidate.toString().toLowerCase().indexOf(getSubstringLowerCase()) > -1;

		return valueToValidate.toString().indexOf(substring) > -1;
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
	}
}
