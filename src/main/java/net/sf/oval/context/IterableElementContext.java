/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

/**
 * @author Sebastian Thomschke
 * @since 3.1
 */
public class IterableElementContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final int elementIndex;

   public IterableElementContext(final Class<?> compileTimeType, final int elementIndex) {
      this.compileTimeType = compileTimeType;
      this.elementIndex = elementIndex;
   }

   public int getElementIndex() {
      return elementIndex;
   }

   @Override
   public String toString() {
      return "[" + elementIndex + "]";
   }
}
