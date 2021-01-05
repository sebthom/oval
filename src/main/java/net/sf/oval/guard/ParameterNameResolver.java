/*
 * Copyright 2005-2021 by Sebastian Thomschke and contributors.
 * SPDX-License-Identifier: EPL-2.0
 */
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
