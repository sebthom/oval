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
package net.sf.oval.exceptions;

import java.util.List;

import net.sf.oval.ConstraintViolation;

/**
 * This exception is thrown if one or more constraints are not satisfied during validation.
 * 
 * @author Sebastian Thomschke
 */
public class ConstraintsViolatedException extends OValException
{
	private static final long serialVersionUID = 1L;

	private final long causingThreadId = Thread.currentThread().getId();

	private final ConstraintViolation[] constraintViolations;

	public ConstraintsViolatedException(final ConstraintViolation... constraintViolations)
	{
		super(constraintViolations.length + " constraint violation(s) found.");

		this.constraintViolations = constraintViolations;
	}

	public ConstraintsViolatedException(final List<ConstraintViolation> constraintViolations)
	{
		super(constraintViolations.size() + " constraint violation(s) found.");

		this.constraintViolations = constraintViolations
				.toArray(new ConstraintViolation[constraintViolations.size()]);
	}

	/**
	 * @return the id of the thread in which the violations occured
	 */
	public long getCausingThreadId()
	{
		return causingThreadId;
	}

	/**
	 * @return the constraintViolations
	 */
	public ConstraintViolation[] getConstraintViolations()
	{
		return constraintViolations;
	}
}