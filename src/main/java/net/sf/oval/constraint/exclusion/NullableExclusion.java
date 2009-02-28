/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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
package net.sf.oval.constraint.exclusion;

import net.sf.oval.Check;
import net.sf.oval.Validator;
import net.sf.oval.configuration.annotation.AbstractAnnotationCheckExclusion;
import net.sf.oval.constraint.NotNullCheck;
import net.sf.oval.context.OValContext;
import net.sf.oval.exception.OValException;

/**
 * @author Sebastian Thomschke
 */
public class NullableExclusion extends AbstractAnnotationCheckExclusion<Nullable>
{
	private static final long serialVersionUID = 1L;

	/**
	 * {@inheritDoc}
	 */
	public boolean isCheckExcluded(final Check check, final Object validatedObject, final Object valueToValidate,
			final OValContext context, final Validator validator) throws OValException
	{
		return check instanceof NotNullCheck;
	}
}
