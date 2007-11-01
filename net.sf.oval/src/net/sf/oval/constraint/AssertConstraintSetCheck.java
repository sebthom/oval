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

import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheck;
import net.sf.oval.context.OValContext;

/**
 * @author Sebastian Thomschke
 */
public class AssertConstraintSetCheck extends AbstractAnnotationCheck<AssertConstraintSet>
{
	private static final long serialVersionUID = 1L;

	private String id;

	@Override
	public void configure(final AssertConstraintSet constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setId(constraintAnnotation.id());
	}

	@Override
	public String getErrorCode()
	{
		throw new UnsupportedOperationException();
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

	@Override
	public int getSeverity()
	{
		throw new UnsupportedOperationException();
	}

	/**
	 *  <b>This method is not used.</b><br>
	 *  The validation of this special constraint is directly performed by the Validator class
	 */
	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context, final Validator validator)
	{
		throw new UnsupportedOperationException();
	}

	@Override
	public void setErrorCode(final String errorCode)
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

	@Override
	public void setSeverity(final int severity)
	{
		throw new UnsupportedOperationException();
	}
}
