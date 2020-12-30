/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
