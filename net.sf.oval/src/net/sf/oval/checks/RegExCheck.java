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
import java.util.regex.Pattern;

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.collections.CollectionFactory;
import net.sf.oval.constraints.RegEx;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class RegExCheck extends AbstractAnnotationCheck<RegEx>
{
	private static final long serialVersionUID = 1L;
	
	private Pattern pattern;

	@Override
	public void configure(final RegEx constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		pattern = Pattern.compile(constraintAnnotation.pattern(), constraintAnnotation.flags());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactory.INSTANCE.createMap(2);
		messageVariables.put("pattern", pattern.pattern());
		return messageVariables;
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		if (value == null) return true;

		return pattern.matcher(value.toString()).matches();
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(final Pattern pattern)
	{
		this.pattern = pattern;
	}
}
