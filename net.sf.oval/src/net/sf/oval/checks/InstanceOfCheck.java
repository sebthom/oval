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

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.constraints.InstanceOf;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class InstanceOfCheck extends AbstractAnnotationCheck<InstanceOf>
{
	private static final long serialVersionUID = 1L;
	
	private Class type;

	@Override
	public void configure(final InstanceOf constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setType(constraintAnnotation.value());
	}

	/**
	 * @return the clazz
	 */
	public Class getType()
	{
		return type;
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{type.getName()};
	}

	public boolean isSatisfied(final Object validatedObject, final Object value, final OValContext context)
	{
		if (value == null) return true;

		return type.isInstance(value);
	}

	/**
	 * @param type the clazz to set
	 */
	public void setType(final Class type)
	{
		this.type = type;
	}

}
