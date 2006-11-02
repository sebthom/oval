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

import net.sf.oval.AbstractAnnotationCheck;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
public class InstanceOfCheck extends AbstractAnnotationCheck<InstanceOf>
{
	private static final long serialVersionUID = 1L;
	
	private Class clazz;

	@Override
	public void configure(final InstanceOf constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setClazz(constraintAnnotation.value());
	}

	/**
	 * @return the clazz
	 */
	public Class getClazz()
	{
		return clazz;
	}

	@Override
	public String[] getMessageValues()
	{
		return new String[]{clazz.getName()};
	}

	public boolean isSatisfied(final Object validatedObject, final Object value, final OValContext context)
	{
		if (value == null) return true;

		return clazz.isInstance(value);
	}

	/**
	 * @param clazz the clazz to set
	 */
	public void setClazz(final Class clazz)
	{
		this.clazz = clazz;
	}

}
