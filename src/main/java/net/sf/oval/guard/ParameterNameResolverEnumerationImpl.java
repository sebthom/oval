/*********************************************************************
 * Copyright 2005-2018 by Sebastian Thomschke and others.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *********************************************************************/
package net.sf.oval.guard;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentMap;

import net.sf.oval.Validator;
import net.sf.oval.exception.ReflectionException;

/**
 * This implementation determines the names of constructor and method parameters by simply enumerating them based on there index:
 * arg0,arg1,arg2,..
 *
 * @author Sebastian Thomschke
 */
public class ParameterNameResolverEnumerationImpl implements ParameterNameResolver {
   private final ConcurrentMap<AccessibleObject, String[]> parameterNamesCache = Validator.getCollectionFactory().createConcurrentMap();

   @Override
   public String[] getParameterNames(final Constructor<?> constructor) throws ReflectionException {
      /*
       * intentionally the following code is not synchronized
       */
      String[] parameterNames = parameterNamesCache.get(constructor);
      if (parameterNames == null) {
         final int parameterCount = constructor.getParameterTypes().length;
         parameterNames = new String[parameterCount];
         for (int i = 0; i < parameterCount; i++) {
            parameterNames[i] = "arg" + i;
         }
         parameterNamesCache.put(constructor, parameterNames);
      }
      return parameterNames;
   }

   @Override
   public String[] getParameterNames(final Method method) throws ReflectionException {
      /*
       * intentionally the following code is not synchronized
       */
      String[] parameterNames = parameterNamesCache.get(method);
      if (parameterNames == null) {
         final int parameterCount = method.getParameterTypes().length;
         parameterNames = new String[parameterCount];
         for (int i = 0; i < parameterCount; i++) {
            parameterNames[i] = "arg" + i;
         }
         parameterNamesCache.put(method, parameterNames);
      }
      return parameterNames;
   }
}
