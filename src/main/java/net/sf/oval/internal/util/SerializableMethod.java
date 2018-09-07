/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
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
import java.lang.reflect.Method;

import net.sf.oval.internal.Log;

/**
 * Serializable Wrapper for java.lang.reflect.Method objects since they do not implement Serializable
 *
 * @author Sebastian Thomschke
 */
public final class SerializableMethod implements Serializable {
   private static final Log LOG = Log.getLog(SerializableMethod.class);

   private static final long serialVersionUID = 1L;

   private final Class<?> declaringClass;
   private transient Method method;
   private final String name;

   private final Class<?>[] parameterTypes;

   public SerializableMethod(final Method method) {
      this.method = method;
      name = method.getName();
      parameterTypes = method.getParameterTypes();
      declaringClass = method.getDeclaringClass();
   }

   public Class<?> getDeclaringClass() {
      return declaringClass;
   }

   public Method getMethod() {
      return method;
   }

   public String getName() {
      return name;
   }

   public Class<?>[] getParameterTypes() {
      return parameterTypes;
   }

   private void readObject(final java.io.ObjectInputStream in) throws IOException, ClassNotFoundException {
      in.defaultReadObject();
      try {
         method = declaringClass.getDeclaredMethod(name, parameterTypes);
      } catch (final NoSuchMethodException ex) {
         LOG.debug("Unexpected NoSuchMethodException occurred.", ex);
         throw new IOException(ex.getMessage());
      }
   }
}
