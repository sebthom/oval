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
package net.sf.oval.constraint;

import java.util.List;
import java.util.Map;

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.internal.CollectionFactoryHolder;
import net.sf.oval.internal.util.ArrayUtils;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class NotMemberOfCheck extends AbstractAnnotationCheck<NotMemberOf>
{
	private static final long serialVersionUID = 1L;

	private boolean ignoreCase;
	private List<String> members;
	private transient List<String> membersLowerCase;

	@Override
	public void configure(final NotMemberOf constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setIgnoreCase(constraintAnnotation.ignoreCase());
		setMembers(constraintAnnotation.value());
	}

	/**
	 * @return the members
	 */
	public List<String> getMembers()
	{
		final List<String> v = CollectionFactoryHolder.getFactory().createList();
		v.addAll(members);
		return v;
	}

	private List<String> getMembersLowerCase()
	{
		if (membersLowerCase == null)
		{
			membersLowerCase = CollectionFactoryHolder.getFactory().createList(members.size());
			for (final String val : members)
			{
				membersLowerCase.add(val.toLowerCase());
			}
		}
		return membersLowerCase;
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
		messageVariables.put("ignoreCase", Boolean.toString(ignoreCase));
		messageVariables.put("members", StringUtils.implode(members, ","));
		return messageVariables;
	}

	/**
	 * @return the ignoreCase
	 */
	public boolean isIgnoreCase()
	{
		return ignoreCase;
	}

	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator)
	{
		if (value == null) return true;

		if (ignoreCase) return !getMembersLowerCase().contains(value.toString().toLowerCase());

		return !members.contains(value.toString());
	}

	/**
	 * @param ignoreCase the ignoreCase to set
	 */
	public void setIgnoreCase(final boolean ignoreCase)
	{
		this.ignoreCase = ignoreCase;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(final List<String> members)
	{
		this.members = CollectionFactoryHolder.getFactory().createList();
		this.members.addAll(members);
		membersLowerCase = null;
	}

	/**
	 * @param members the members to set
	 */
	public void setMembers(final String... members)
	{
		this.members = CollectionFactoryHolder.getFactory().createList();
		ArrayUtils.addAll(this.members, members);
		membersLowerCase = null;
	}
}
