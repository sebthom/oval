/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.oval.ConstraintTarget;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.ArrayUtils;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternCheck extends AbstractAnnotationCheck<MatchPattern>
{
	private static final long serialVersionUID = 1L;

	private final List<Pattern> patterns = getCollectionFactory().createList(2);
	private boolean matchAll = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final MatchPattern constraintAnnotation)
	{
		super.configure(constraintAnnotation);

		setMatchAll(constraintAnnotation.matchAll());

		synchronized (patterns)
		{
			patterns.clear();
			final String[] stringPatterns = constraintAnnotation.pattern();
			final int[] f = constraintAnnotation.flags();
			for (int i = 0, l = stringPatterns.length; i < l; i++)
			{
				final int flag = f.length > i ? f[i] : 0;
				final Pattern p = Pattern.compile(stringPatterns[i], flag);
				patterns.add(p);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	protected ConstraintTarget[] getAppliesToDefault()
	{
		return new ConstraintTarget[]{ConstraintTarget.VALUES};
	}
	
	/**
	 * @return the pattern
	 */
	public Pattern[] getPatterns()
	{
		synchronized (patterns)
		{
			return patterns.toArray(new Pattern[patterns.size()]);
		}
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

		for (final Pattern p : patterns)
		{
			final boolean matches = p.matcher(valueToValidate.toString()).matches();

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
	 * @param pattern the pattern to set
	 */
	public void setPattern(final Pattern pattern)
	{
		synchronized (patterns)
		{
			patterns.clear();
			patterns.add(pattern);
		}
		requireMessageVariablesRecreation();
	}

	/**
	 * @param pattern the pattern to set
	 */
	public void setPattern(final String pattern, final int flags)
	{
		synchronized (patterns)
		{
			patterns.clear();
			patterns.add(Pattern.compile(pattern, flags));
		}
		requireMessageVariablesRecreation();
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(final Collection<Pattern> patterns)
	{
		synchronized (this.patterns)
		{
			this.patterns.clear();
			this.patterns.addAll(patterns);
		}
		requireMessageVariablesRecreation();
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(final Pattern... patterns)
	{
		synchronized (this.patterns)
		{
			this.patterns.clear();
			ArrayUtils.addAll(this.patterns, patterns);
		}
		requireMessageVariablesRecreation();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> createMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
		messageVariables.put("pattern", patterns.size() == 1 ? patterns.get(0).toString() : patterns.toString());
		return messageVariables;
	}
}
