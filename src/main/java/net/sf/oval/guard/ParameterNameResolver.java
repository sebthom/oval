/*********************************************************************
 * Copyright 2005-2020 by Sebastian Thomschke and others.
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

import net.sf.oval.exception.ReflectionException;

/**
 * @author Sebastian Thomschke
 */
public interface ParameterNameResolver {
   /**
    * Returns an empty String array if no parameters are declared.
    * 
    * @param constructor the constructor
    * @return an array holding the parameter names of the given constructor.
    * @throws ReflectionException in case retrieving the parameter names fails
    */
   String[] getParameterNames(Constructor<?> constructor) throws ReflectionException;

   /**
    * Returns an empty String array if no parameters are declared
    * 
    * @param method the method
    * @return an array holding the parameter names of the given method.
    * @throws ReflectionException in case retrieving the parameter names fails
    */
   String[] getParameterNames(Method method) throws ReflectionException;
}
