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
package net.sf.oval;

import java.lang.reflect.Field;
import java.util.Set;

import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.OValException;

/**
 * @author Sebastian Thomschke
 */
class ConstraintSet
{
	/**
	 * considered if isDynamic is false
	 */
	Set<Check> checks;

	/**
	 * considered if isDynamic=true
	 * the context where the constraint set was defined
	 */
	OValContext context;

	/**
	 * the id of the constraint set
	 */
	String id;

	/**
	 * @return Returns a set of constraint checks associated with this constraint set
	 */
	Set<Check> getChecks(final Validator validator) throws OValException
	{
		if (context != null)
		{
			if (context instanceof FieldContext)
			{
				final FieldContext fc = (FieldContext) context;
				final Field f = fc.getField();
				final ClassChecks cf = validator.getClassChecks(f.getDeclaringClass());

				// for performance reasons we are returning the internal set
				return cf.checksByField.get(f);
			}

			throw new OValException("Currently unsupported context type " + context);
		}

		return checks;
	}
}
