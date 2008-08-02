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

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternsCheck extends AbstractAnnotationCheck<MatchPatterns>
{
	private static final long serialVersionUID = 1L;

	private MatchPatternCheck[] patterns;
	private boolean matchAll = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final MatchPatterns constraintAnnotation)
	{
		super.configure(constraintAnnotation);

		setMatchAll(constraintAnnotation.matchAll());
		setPatterns(constraintAnnotation.patterns());
	}

	/**
	 * @return the patterns
	 */
	public MatchPatternCheck[] getPatterns()
	{
		return patterns;
	}

	/**
	 * @return the matchAll
	 */
	public boolean isMatchAll()
	{
		return matchAll;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		for (final MatchPatternCheck p : patterns)
		{
			final boolean matches = p.isSatisfied(validatedObject, valueToValidate, context, validator);

			if (matches)
			{
				if (!matchAll) return true;
			}
			else
			{
				if (matchAll) return false;
			}
		}
		return matchAll ? true : false;
	}

	/**
	 * @param matchAll the matchAll to set
	 */
	public void setMatchAll(final boolean matchAll)
	{
		this.matchAll = matchAll;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param patterns the patterns to set
	 */
	private void setPatterns(final MatchPattern[] patterns)
	{
		final MatchPatternCheck[] patternChecks = new MatchPatternCheck[patterns.length];
		for (int i = 0; i < patterns.length; i++)
		{
			patternChecks[i] = new MatchPatternCheck();
			patternChecks[i].configure(patterns[i]);
		}
		setPatterns(patternChecks);
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(final MatchPatternCheck[] patterns)
	{
		this.patterns = patterns;
		requireMessageVariablesRecreation();
	}

}
