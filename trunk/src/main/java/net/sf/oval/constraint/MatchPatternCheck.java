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

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.CollectionFactoryHolder;
import net.sf.oval.internal.util.ArrayUtils;

/**
 * @author Sebastian Thomschke
 */
public class MatchPatternCheck extends AbstractAnnotationCheck<MatchPattern>
{
	private static final long serialVersionUID = 1L;

	private final List<Pattern> patterns = CollectionFactoryHolder.getFactory().createList(2);
	private boolean matchAll = true;

	/**
	 * @return the matchAll
	 */
	public boolean isMatchAll()
	{
		return matchAll;
	}

	/**
	 * @param matchAll the matchAll to set
	 */
	public void setMatchAll(final boolean matchAll)
	{
		this.matchAll = matchAll;
	}

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

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
		messageVariables.put("pattern", patterns.size() == 1 ? patterns.get(0).toString()
				: patterns.toString());
		return messageVariables;
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

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator)
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
	 * @param pattern the pattern to set
	 */
	public void setPattern(final Pattern pattern)
	{
		synchronized (patterns)
		{
			patterns.clear();
			patterns.add(pattern);
		}
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
	}

	/**
	 * @param patterns the patterns to set
	 */
	public void setPatterns(final Pattern[] patterns)
	{
		synchronized (this.patterns)
		{
			this.patterns.clear();
			ArrayUtils.addAll(this.patterns, patterns);
		}
	}
}