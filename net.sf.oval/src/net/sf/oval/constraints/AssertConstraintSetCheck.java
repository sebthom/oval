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
 */
public class AssertConstraintSetCheck extends AbstractAnnotationCheck<AssertConstraintSet>
{
	private static final long serialVersionUID = 1L;

	private String id;

	/**
	 * The class in which the constraint set is defined.
	 * If it is Object.class then a global constraint set is referenced.
	 */
	private Class source = Object.class;

	@Override
	public void configure(final AssertConstraintSet constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setId(constraintAnnotation.id());
		setSource(constraintAnnotation.source());
	}

	public String getId()
	{
		return id;
	}

	@Override
	public String getMessage()
	{
		throw new UnsupportedOperationException();
	}

	public Class getSource()
	{
		return source == null ? Object.class : source;
	}

	/**
	 *  <b>This method is not used.</b><br>
	 *  The validation of this special constraint is directly performed by the Validator class
	 */
	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		throw new UnsupportedOperationException();
	}

	public void setId(final String id)
	{
		this.id = id;
	}

	@Override
	public void setMessage(final String message)
	{
		throw new UnsupportedOperationException();
	}
	
	public void setSource(final Class source)
	{
		this.source = source;
	}	
}
