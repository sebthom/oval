/*
 * Created on 03.11.2006
 */
package net.sf.oval;

import java.lang.reflect.Field;
import java.util.Set;

import net.sf.oval.contexts.FieldContext;
import net.sf.oval.contexts.OValContext;
import net.sf.oval.exceptions.OValException;

/**
 * @author Sebastian Thomschke
 *
 */
class AnnotationConstraintSet extends ConstraintSet
{
	/**
	 * the context where the constraint set was defined
	 */
	OValContext context;

	@Override
	public Set<Check> getChecks(final Validator validator) throws OValException
	{
		if (context instanceof FieldContext)
		{
			final FieldContext fc = (FieldContext) context;
			final Field f = fc.getField();
			final ClassConfiguration cf = validator.getClassConfig(f.getDeclaringClass());

			// for performance reasons we are returning the internal set
			return cf.checksByField.get(f);
		}

		throw new OValException("Currently unsupported context type " + context);
	}
}
