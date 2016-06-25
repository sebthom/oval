/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2016 Sebastian
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
public enum ConstraintTarget {
    /**
     * apply the constraint to the keys of a map
     */
    KEYS,

    /**
     * apply the constraint to the values of a the map / the items of the collection / the elements of the array
     */
    VALUES,

    /**
     * recursively apply the constraint to items of keys and values in case they are lists or arrays themselves
     */
    RECURSIVE,

    /**
     * apply the constraint to the collection / map / array object itself
     */
    CONTAINER
}
