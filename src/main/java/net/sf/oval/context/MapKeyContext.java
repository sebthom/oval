/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

/**
 * @author Sebastian Thomschke
 * @since 3.1
 */
public class MapKeyContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final Object key;

   public MapKeyContext(final Class<?> compileTimeType, final Object key) {
      this.compileTimeType = compileTimeType;
      this.key = key;
   }

   public Object getKey() {
      return key;
   }

   @Override
   public String toString() {
      if (key instanceof String)
         return "[\"" + key + "\"]";
      if (key instanceof Character)
         return "['" + key + "']";
      return "[" + key + "]";
   }
}
