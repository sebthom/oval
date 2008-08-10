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
public class AssertValidCheck extends AbstractAnnotationCheck<AssertValid>
{
	private static final long serialVersionUID = 1L;

	private boolean requireValidElements = true;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void configure(final AssertValid constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setRequireValidElements(constraintAnnotation.requireValidElements());
	}

	/**
	 * @return true if all elements of a collection must be valid 
	 */
	public boolean isRequireValidElements()
	{
		return requireValidElements;
	}

	/**
	 *  <b>This method is not used.</b><br>
	 *  The validation of this special constraint is directly performed by the Validator class
	 *  @throws UnsupportedOperationException always thrown if this method is invoked
	 */
	public boolean isSatisfied(final Object validatedObject, final Object valueToValidate, final OValContext context,
			final Validator validator) throws UnsupportedOperationException
	{
		throw new UnsupportedOperationException();
	}

	/**
	 * Specifies if all the elements of a collection must be valid.
	 */
	public void setRequireValidElements(final boolean requireValidElements)
	{
		this.requireValidElements = requireValidElements;
	}
}
