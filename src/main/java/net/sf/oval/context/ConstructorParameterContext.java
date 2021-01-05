/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
package net.sf.oval.context;

import java.lang.reflect.Constructor;

import net.sf.oval.Validator;
import net.sf.oval.internal.util.SerializableConstructor;
import net.sf.oval.internal.util.StringUtils;

/**
 * @author Sebastian Thomschke
 */
public class ConstructorParameterContext extends OValContext {
   private static final long serialVersionUID = 1L;

   private final SerializableConstructor constructor;
   private final int parameterIndex;
   private final String parameterName;

   public ConstructorParameterContext(final Constructor<?> constructor, final int parameterIndex, final String parameterName) {
      this.constructor = new SerializableConstructor(constructor);
      this.parameterIndex = parameterIndex;
      this.parameterName = parameterName;
      compileTimeType = constructor.getParameterTypes()[parameterIndex];
   }

   public Constructor<?> getConstructor() {
      return constructor.getConstructor();
   }

   @Override
   public Class<?> getDeclaringClass() {
      return constructor.getDeclaringClass();
   }

   public int getParameterIndex() {
      return parameterIndex;
   }

   public String getParameterName() {
      return parameterName;
   }

   @Override
   public String toString() {
      return constructor.getDeclaringClass().getName() + "." + toStringUnqualified();
   }

   @Override
   public String toStringUnqualified() {
      return "<init>(" + StringUtils.join(constructor.getParameterTypes(), ',') + ") " //
         + Validator.getMessageResolver().getMessage("net.sf.oval.context.ConstructorParameterContext.parameter") + " " //
         + parameterIndex //
         + (parameterName == null || parameterName.length() == 0 ? "" : " (" + parameterName + ")");
   }
}
