/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2009 Sebastian
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

/** 
 * @author Sebastian Thomschke
 */
public enum ConstraintTarget
{
	/**
	 * apply the constraint to the keys of a map
	 */
	KEYS,

	/**
	 * apply the constraint to the values of a the map / the items of the collection / the elements of the array
	 */
	VALUES,

	/**
	 * apply the constraint to the collection / map / array object itself
	 */
	CONTAINER
}
