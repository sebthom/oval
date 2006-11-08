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
package net.sf.oval.configuration.elements;

import java.util.List;

import net.sf.oval.Check;

/**
 * @author Sebastian Thomschke
 */
public class ConstraintSetConfiguration
{
	public List<Check> checks;

	/**
	 * the id of the constraint set
	 */
	public String id;
}
