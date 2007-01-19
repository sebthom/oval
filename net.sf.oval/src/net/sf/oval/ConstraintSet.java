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
import java.util.Collection;
import java.util.Set;

import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.OValException;

/**
 * @author Sebastian Thomschke
 */
class ConstraintSet
{
	Set<Check> checks;

	/**
	 * the context where to get the checks from
	 */
	OValContext context;

	String localId;
	
	/**
	 * the id of the constraint set
	 */
	String id;

	/**
	 * @return Returns a set of constraint checks associated with this constraint set
	 */
	Collection<Check> getChecks(final Validator validator) throws OValException
	{
		if (context != null)
		{
			if (context instanceof FieldContext)
			{
				final FieldContext fc = (FieldContext) context;
				final Field f = fc.getField();

				// for performance reasons we are returning the internal set
				final ClassChecks cc = validator.getClassChecks(f.getDeclaringClass());
				return cc.checksForFields.get(f);
			}

			throw new OValException("Currently unsupported context type " + context);
		}

		return checks;
	}
}
