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

import java.util.Arrays;
import java.util.List;

import net.sf.oval.exceptions.ConstraintsViolatedException;
import net.sf.oval.utils.CollectionFactory;

/**
 * @author Sebastian Thomschke
 * @version $Revision: 1.1 $
 */
public class ConstraintsViolatedAdapter implements ConstraintsViolatedListener
{
	private final List<ConstraintsViolatedException> violationExceptions = CollectionFactory
			.createList();
	private final List<ConstraintViolation> violations = CollectionFactory.createList();

	public void clear()
	{
		violationExceptions.clear();
		violations.clear();
	}

	/**
	 * @return Returns the constraint violation exceptions.
	 */
	public List<ConstraintsViolatedException> getConstraintsViolatedExceptions()
	{
		return violationExceptions;
	}

	/**
	 * @return Returns the constraint violations.
	 */
	public List<ConstraintViolation> getConstraintViolations()
	{
		return violations;
	}

	public void onConstraintsViolatedException(final ConstraintsViolatedException exception)
	{
		violationExceptions.add(exception);
		violations.addAll(Arrays.asList(exception.getConstraintViolations()));
	}
}
