/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
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
      this.method = SerializableMethod.getInstance(method);
      this.parameterIndex = parameterIndex;
      this.parameterName = parameterName == null ? "param" + parameterIndex : parameterName;
      compileTimeType = method.getParameterTypes()[parameterIndex];
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
      return method.getDeclaringClass().getName() + "." + method.getName() + "(" + StringUtils.implode(method.getParameterTypes(), ",") + ") " + Validator
         .getMessageResolver().getMessage("net.sf.oval.context.MethodParameterContext.parameter") + " " + parameterIndex + (parameterName == null
            || parameterName.length() == 0 ? "" : " (" + parameterName + ")");
   }
}
