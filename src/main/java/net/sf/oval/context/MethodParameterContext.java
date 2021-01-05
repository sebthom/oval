/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

import java.lang.reflect.Method;

import net.sf.oval.Validator;
import net.sf.oval.internal.util.SerializableMethod;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class MethodParameterContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final SerializableMethod method;
   private final int parameterIndex;
   private final String parameterName;

   public MethodParameterContext(final Method method, final int parameterIndex, final String parameterName) {
      this.method = new SerializableMethod(method);
      this.parameterIndex = parameterIndex;
      this.parameterName = parameterName == null ? "param" + parameterIndex : parameterName;
      compileTimeType = method.getParameterTypes()[parameterIndex];
   }

   @Override
   public Class<?> getDeclaringClass() {
      return method.getDeclaringClass();
   }

   public Method getMethod() {
      return method.getMethod();
   }

   public int getParameterIndex() {
      return parameterIndex;
   }

   public String getParameterName() {
      return parameterName;
   }

   @Override
   public String toString() {
      return method.getDeclaringClass().getName() + "." + toStringUnqualified();
   }

   @Override
   public String toStringUnqualified() {
      return method.getName() + "(" + StringUtils.join(method.getParameterTypes(), ',') + ") " //
         + Validator.getMessageResolver().getMessage("net.sf.oval.context.MethodParameterContext.parameter") + " " //
         + parameterIndex //
         + (parameterName == null || parameterName.length() == 0 ? "" : " (" + parameterName + ")");
   }
}
