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
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class InstanceOfCheck extends AbstractAnnotationCheck<InstanceOf>
{
	private static final long serialVersionUID = 1L;

	private Class< ? >[] types;

	@Override
	public void configure(final InstanceOf constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setTypes(constraintAnnotation.value());
	}

	@Override
	public Map<String, String> getMessageVariables()
	{
		final Map<String, String> messageVariables = CollectionFactoryHolder.getFactory()
				.createMap(2);
		if (types.length == 1)
		{
			messageVariables.put("types", types[0].getName());
		}
		else
		{
			final String[] classNames = new String[types.length];
			for (int i = 0, l = classNames.length; i < l; i++)
			{
				classNames[i] = types[i].getName();
			}
			messageVariables.put("types", StringUtils.implode(classNames, ","));
		}
		return messageVariables;
	}

	/**
	 * @return the type
	 */
	public Class< ? >[] getTypes()
	{
		return types;
	}

	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator)
	{
		if (valueToValidate == null) return true;

		for (final Class< ? > type : types)
		{
			if (!type.isInstance(valueToValidate)) return false;
		}
		return true;
	}

	/**
	 * @param types the types to set
	 */
	public void setTypes(final Class< ? >... types)
	{
		this.types = types;
	}
}
