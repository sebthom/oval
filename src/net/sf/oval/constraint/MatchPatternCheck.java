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
import java.util.regex.Pattern;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.CollectionFactoryHolder;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternCheck extends AbstractAnnotationCheck<MatchPattern>
{
	private static final long serialVersionUID = 1L;

	private Pattern pattern;

	@Override
	public void configure(final MatchPattern constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setPattern(constraintAnnotation.pattern(), constraintAnnotation.flags());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
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

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator)
	{
		if (valueToValidate == null) return true;

		return pattern.matcher(valueToValidate.toString()).matches();
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(final Pattern pattern)
	{
		this.pattern = pattern;
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(final String pattern, final int flags)
	{
		this.pattern = Pattern.compile(pattern, flags);
	}
}
