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
public class AssertValidCheck extends AbstractAnnotationCheck<AssertValid>
{
	private static final long serialVersionUID = 1L;

	private boolean requireValidElements = true;

	@Override
	public void configure(AssertValid constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setRequireValidElements(constraintAnnotation.requireValidElements());
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

	/**
	 * @return true if all elements of a collection must be valid 
	 */
	public boolean isRequireValidElements()
	{
		return requireValidElements;
	}

	/**
	 * Specifies if all the elements of a collection must be valid.
	 */
	public void setRequireValidElements(final boolean requireValidElements)
	{
		this.requireValidElements = requireValidElements;
	}
}
