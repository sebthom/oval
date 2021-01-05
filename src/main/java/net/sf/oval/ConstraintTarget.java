/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
