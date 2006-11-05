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
	protected Set<Check> checks;

	/**
	 * the id of the constraint set
	 */
	public String id;

	/**
	 * @return Returns a set of constraint checks associated with this constraint set
	 */
	public Set<Check> getChecks(final Validator validator) throws OValException
	{
		return checks;
	}
}
