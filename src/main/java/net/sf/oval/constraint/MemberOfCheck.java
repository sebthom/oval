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

import static net.sf.oval.Validator.getCollectionFactory;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.util.ArrayUtils;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class MemberOfCheck extends AbstractAnnotationCheck<MemberOf>
{
	private static final long serialVersionUID = 1L;

	private boolean ignoreCase;
	private List<String> members;
	private transient List<String> membersLowerCase;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final MemberOf constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setIgnoreCase(constraintAnnotation.ignoreCase());
		setMembers(constraintAnnotation.value());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, String> createMessageVariables()
	{
		final Map<String, String> messageVariables = getCollectionFactory().createMap(2);
		messageVariables.put("ignoreCase", Boolean.toString(ignoreCase));
		messageVariables.put("members", StringUtils.implode(members, ","));
		return messageVariables;
	}

	/**
	 * @return the members
	 */
	public List<String> getMembers()
	{
		final List<String> v = getCollectionFactory().createList();
		v.addAll(members);
		return v;
	}

	private List<String> getMembersLowerCase()
	{
		if (membersLowerCase == null)
		{
			membersLowerCase = getCollectionFactory().createList(members.size());
			for (final String val : members)
			{
				membersLowerCase.add(val.toLowerCase(Locale.getDefault()));
			}
		}
		return membersLowerCase;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator)
	{
		if (valueToValidate == null) return true;

		if (ignoreCase)
			return getMembersLowerCase().contains(valueToValidate.toString().toLowerCase(Locale.getDefault()));

		return members.contains(valueToValidate.toString());
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(final boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(final List<String> members)
	{
		this.members = getCollectionFactory().createList();
		this.members.addAll(members);
		membersLowerCase = null;
		requireMessageVariablesRecreation();
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(final String... members)
	{
		this.members = getCollectionFactory().createList();
		ArrayUtils.addAll(this.members, members);
		membersLowerCase = null;
		requireMessageVariablesRecreation();
	}
}
