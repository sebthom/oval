/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

/**
 * @author Sebastian Thomschke
 * @since 3.1
 */
public class ObjectGraphNavigationContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final String path;

   public ObjectGraphNavigationContext(final String path) {
      this.path = path;
   }

   public String getPath() {
      return path;
   }

   @Override
   public String toString() {
      return path;
   }
}
