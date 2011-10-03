/*******************************************************************************
 * Portions created by Sebastian Thomschke are copyright (c) 2005-2011 Sebastian
 * Thomschke.
 * 
 * All Rights Reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Sebastian Thomschke - initial implementation.
 *******************************************************************************/
package net.sf.oval.guard;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import net.sf.oval.exception.ReflectionException;

/**
 * @author Sebastian Thomschke
 */
public interface ParameterNameResolver
{
	/**
	 * Returns an empty String array if no parameters are declared.
	 * @param constructor the constructor
	 * @return an array holding the parameter names of the given constructor.
	 * @throws ReflectionException in case retrieving the parameter names fails
	 */
	String[] getParameterNames(Constructor< ? > constructor) throws ReflectionException;

	/**
	 * Returns an empty String array if no parameters are declared
	 * @param method the method
	 * @return an array holding the parameter names of the given method.
	 * @throws ReflectionException in case retrieving the parameter names fails
	 */
	String[] getParameterNames(Method method) throws ReflectionException;
}
