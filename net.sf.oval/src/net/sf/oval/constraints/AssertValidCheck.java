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

import java.util.List;

import net.sf.oval.AbstractCheck;
import net.sf.oval.ConstraintViolation;
import net.sf.oval.Validator;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.6 $
 */
public class AssertValidCheck extends AbstractCheck<AssertTrue>
{
	public boolean isSatisfied(final Object validatedObject, final Object value)
	{
		if (value == null) return true;

		// ignore circular dependencies
		if (Validator.isCurrentlyValidated(value)) return true;

		final List<ConstraintViolation> violations = Validator.validate(value);

		return violations.size() == 0;
	}
}
