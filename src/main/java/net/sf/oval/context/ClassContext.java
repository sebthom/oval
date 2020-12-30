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
 */
public class ClassContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final Class<?> clazz;

   public ClassContext(final Class<?> clazz) {
      this.clazz = clazz;
      compileTimeType = clazz;
   }

   public Class<?> getClazz() {
      return clazz;
   }

   /**
    * @since 3.1
    */
   @Override
   public Class<?> getDeclaringClass() {
      return clazz;
   }

   @Override
   public String toString() {
      return clazz.getName();
   }
}
