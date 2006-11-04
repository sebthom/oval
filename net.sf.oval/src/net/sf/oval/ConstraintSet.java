/*
 * Created on 03.11.2006
 */
package net.sf.oval;

import java.util.Set;

import net.sf.oval.exceptions.OValException;

/**
 * @author Sebastian Thomschke
 *
 */
class ConstraintSet
{
	/**
	 * the short id of the constraint set
	 */
	String shortId;

	/**
	 * the fully qualified id of the constraint set
	 */
	String id;

	/**
	 * @return Returns a set of constraint checks associated with this constraint set
	 */
	Set<Check> getChecks(final Validator validator) throws OValException
	{
		return null;
	}
}
