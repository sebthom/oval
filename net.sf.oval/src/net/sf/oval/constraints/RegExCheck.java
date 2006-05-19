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

import java.util.regex.Pattern;

import net.sf.oval.AbstractCheck;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.7 $
 */
public class RegExCheck extends AbstractCheck<RegEx>
{
	private Pattern pattern;

	@Override
	public void configure(final RegEx constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		pattern = Pattern.compile(constraintAnnotation.pattern(), constraintAnnotation.flags());
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{pattern.pattern()};
	}

	/**
	 * @return the pattern
	 */
	public Pattern getPattern()
	{
		return pattern;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value)
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
