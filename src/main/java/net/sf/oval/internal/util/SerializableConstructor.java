/*********************************************************************
 * Copyright 2005-2019 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.internal.util;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.Constructor;

import net.sf.oval.internal.Log;

/**
 * Serializable Wrapper for java.lang.reflect.Constructor objects since they do not implement Serializable
 *
 * @author Sebastian Thomschke
 */
public final class SerializableConstructor implements Serializable {
   private static final Log LOG = Log.getLog(SerializableConstructor.class);

   private static final long serialVersionUID = 1L;

   private transient Constructor<?> constructor;
   private final Class<?> declaringClass;
   private final Class<?>[] parameterTypes;

   public SerializableConstructor(final Constructor<?> constructor) {
      this.constructor = constructor;
      parameterTypes = constructor.getParameterTypes();
      declaringClass = constructor.getDeclaringClass();
   }

   public Constructor<?> getConstructor() {
      return constructor;
   }

   public Class<?> getDeclaringClass() {
      return declaringClass;
   }

   public Class<?>[] getParameterTypes() {
      return parameterTypes;
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      try {
         constructor = declaringClass.getDeclaredConstructor(parameterTypes);
      } catch (final NoSuchMethodException ex) {
         LOG.debug("Unexpected NoSuchMethodException occurred", ex);
         throw new IOException(ex.getMessage());
      }
   }
}
