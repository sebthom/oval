/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

import java.io.Serializable;

/**
 * The root class of the validation context classes.
 *
 * @author Sebastian Thomschke
 */
public abstract class OValContext implements Serializable {
   private static final long serialVersionUID = 1L;

   protected Class<?> compileTimeType;

   /**
    * May return null if not applicable.
    */
   public Class<?> getCompileTimeType() {
      return compileTimeType;
   }

   /**
    * May return null if not applicable.
    *
    * @since 3.1
    */
   public Class<?> getDeclaringClass() {
      return null;
   }

   /**
    * @since 3.1
    */
   public String toStringUnqualified() {
      return toString();
   }
}
