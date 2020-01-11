/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
