/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

import java.lang.reflect.Method;

import net.sf.oval.internal.util.SerializableMethod;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class MethodEntryContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final SerializableMethod method;

   public MethodEntryContext(final Method method) {
      this.method = new SerializableMethod(method);
   }

   @Override
   public Class<?> getDeclaringClass() {
      return method.getDeclaringClass();
   }

   public Method getMethod() {
      return method.getMethod();
   }

   @Override
   public String toString() {
      return method.getDeclaringClass().getName() + "." + toStringUnqualified();
   }

   @Override
   public String toStringUnqualified() {
      return method.getName() + "(" + StringUtils.join(method.getParameterTypes(), ',') + ")";
   }
}
