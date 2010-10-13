/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2010 Sebastian
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
package net.sf.oval.exception;

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

	/**
	 * @param constraintViolations must not be null
	 */
	public ConstraintsViolatedException(final ConstraintViolation... constraintViolations)
	{
		// the message of the first occurring constraint violation will be used
		super(constraintViolations[0].getMessage());

		this.constraintViolations = constraintViolations;
	}

	/**
	 * @param constraintViolations must not be null
	 */
	public ConstraintsViolatedException(final List<ConstraintViolation> constraintViolations)
	{
		// the message of the first occurring constraint violation will be used
		super(constraintViolations.get(0).getMessage());

		this.constraintViolations = constraintViolations.toArray(new ConstraintViolation[constraintViolations.size()]);
	}

	/**
	 * @return the id of the thread in which the violations occurred
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
		return constraintViolations.clone();
	}
}