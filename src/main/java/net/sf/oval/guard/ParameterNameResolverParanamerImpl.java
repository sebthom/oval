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

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import com.thoughtworks.paranamer.BytecodeReadingParanamer;
import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

import net.sf.oval.exception.ReflectionException;

/**
 * This implementation that uses com.thoughtworks.paranamer.Paranamer (http://paranamer.codehaus.org/) to determine the names of parameter names as a fallback
 * the results of a ParameterNameResolverEnumerationImpl are returned
 *
 * @author Sebastian Thomschke
 */
public class ParameterNameResolverParanamerImpl implements ParameterNameResolver {
   private static final ParameterNameResolver FALLBACK = new ParameterNameResolverEnumerationImpl();

   private final Paranamer paranamer;

   public ParameterNameResolverParanamerImpl() {
      paranamer = new CachingParanamer(new BytecodeReadingParanamer());
   }

   public ParameterNameResolverParanamerImpl(final Paranamer paranamer) {
      this.paranamer = paranamer;
   }

   @Override
   public String[] getParameterNames(final Constructor<?> constructor) throws ReflectionException {
      return FALLBACK.getParameterNames(constructor);
   }

   @Override
   public String[] getParameterNames(final Method method) throws ReflectionException {
      final String[] parameterNames = paranamer.lookupParameterNames(method);

      if (parameterNames == null)
         return FALLBACK.getParameterNames(method);

      return parameterNames;
   }
}
