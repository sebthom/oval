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

   public int getParameterIndex() {
      return parameterIndex;
   }

   public String getParameterName() {
      return parameterName;
   }

   @Override
   public String toString() {
      return constructor.getDeclaringClass().getName() + "(" + StringUtils.implode(constructor.getParameterTypes(), ",") + ") " + Validator.getMessageResolver()
         .getMessage("net.sf.oval.context.ConstructorParameterContext.parameter") + " " + parameterIndex + (parameterName == null || parameterName.length() == 0
            ? ""
            : " (" + parameterName + ")");
   }
}
