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

import net.sf.oval.AbstractCheck;
import net.sf.oval.contexts.OValContext;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
public class FieldConstraintsCheck extends AbstractCheck<FieldConstraints>
{
	private String fieldName;

	@Override
	public void configure(final FieldConstraints constraintAnnotation)
	{
		super.configure(constraintAnnotation);
		setFieldName(constraintAnnotation.value());
	}

	/**
	 * @return the fieldName
	 */
	public String getFieldName()
	{
		return fieldName;
	}

	/**
	 *  This method is not used.
	 *  The validation of this special constraint is directly performed by the Validator class
	 */
	public boolean isSatisfied(final Object validatedObject, final Object value,
			final OValContext context)
	{
		return true;
	}

	/**
	 * @param fieldName the fieldName to set
	 */
	public void setFieldName(final String fieldName)
	{
		this.fieldName = fieldName;
	}
}
